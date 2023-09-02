package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.util.FilmSortingCriteria.FilmSortingCriteria;

import java.util.*;

import static ru.yandex.practicum.filmorate.storage.Mapping.*;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final String commonSQLPartForReading = "SELECT f.id, name, description, release_date, duration, mpa, " +
            "COUNT(lf.id_user) AS rate, gf.id_genre, g.name_genre, fd.director_id, d.director_name " +
            "FROM film as f " +
            "LEFT JOIN like_film AS lf ON f.id = lf.id_film " +
            "LEFT JOIN genre_film AS gf ON f.id = gf.id_film " +
            "LEFT JOIN genre AS g ON gf.id_genre = g.id_genre " +
            "LEFT JOIN films_directors AS fd ON f.id = fd.film_id " +
            "LEFT JOIN directors AS d ON d.director_id = fd.director_id ";
    JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        log.debug("Выполняем getFilms()");
        return jdbcTemplate.query(commonSQLPartForReading +
                        "GROUP BY f.id, gf.id_genre, fd.director_id " +
                        "ORDER BY f.id, id_genre, director_id", mapperGetFilms()).stream()
                .findFirst()
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Film> getSortedFilmsOfDirector(int directorId,
                                               FilmSortingCriteria criteria) {
        log.debug("Выполняем getSortedFilmsOfDirector({}, {})", directorId, criteria.name());
        String sql = commonSQLPartForReading +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id " +
                criteria.getSqlPart();

        if (Boolean.FALSE.equals(jdbcTemplate.queryForObject(
                "SELECT EXISTS (SELECT director_id FROM directors WHERE director_id = ?)",
                Boolean.class, directorId))) {
            log.warn("Режиссера с таким id не существует {}", directorId);
            throw new DirectorNotFoundException(String.format("Режиссер с id = %d не найден", directorId));
        }

        return jdbcTemplate.query(sql, mapperGetFilms(), directorId).stream()
                .findFirst()
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        log.debug("Выполняем searchFilms({}, {})", query, by);
        boolean isSearchByFilm = by.contains("title");

        boolean isSearchByDirector = by.contains("director");

        String where = isSearchByFilm && isSearchByDirector ?
                "WHERE LOWER(f.name) LIKE LOWER(?) OR LOWER(d.director_name) LIKE LOWER(?) " :
                isSearchByFilm ?
                        "WHERE LOWER(f.name) LIKE LOWER(?) " :
                        "WHERE LOWER(d.director_name) LIKE LOWER(?) ";

        String sql = commonSQLPartForReading + where +
                "GROUP BY f.id, gf.id_genre, fd.director_id " +
                "ORDER BY f.rate";

        return isSearchByFilm && isSearchByDirector ?
                jdbcTemplate.query(sql, mapperGetFilms(),
                                wrapInPercent(query), wrapInPercent(query)).stream()
                        .findFirst()
                        .orElse(Collections.emptyList()) :
                jdbcTemplate.query(sql, mapperGetFilms(),
                                wrapInPercent(query)).stream()
                        .findFirst()
                        .orElse(Collections.emptyList());
    }

    @Override
    public List<Film> popularGenreYearSearch(int genreId, int year, int limit) {
        log.debug("Выполняем popularGenreYearSearch({}, {}, {})", genreId, year, limit);

        final String subQuery = "SELECT id_film FROM genre_film AS gf WHERE gf.id_genre = ?";

        final String where = "WHERE EXTRACT(YEAR FROM f.release_date) = ? AND id IN (" + subQuery + ") " +
                "GROUP BY f.id, gf.id_genre, fd.director_id " +
                "ORDER BY rate DESC " +
                "LIMIT ?";
        final String sql = commonSQLPartForReading + where;

        return jdbcTemplate.query(sql, mapperGetFilms(), year, genreId, limit).stream()
                .findFirst()
                .orElse(Collections.emptyList());
    }
    @Override
    public List<Film> popularGenreSearch(int genreId, int limit) {
        log.debug("Выполняем popularGenreSearch({}, {})", genreId, limit);

        final String subQuery = "SELECT id_film FROM genre_film AS gf WHERE gf.id_genre = ?";
        final String where = "WHERE id IN (" + subQuery + ") " +
                "GROUP BY f.id, gf.id_genre, fd.director_id " +
                "ORDER BY rate DESC " +
                "LIMIT ?";
        final String sql = commonSQLPartForReading + where;

        return jdbcTemplate.query(sql, mapperGetFilms(), genreId, limit).stream()
                .findFirst()
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Film> popularYearSearch(int year, int limit) {
        log.debug("Выполняем popularYearSearch({}, {})", year, limit);

        final String where = "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                "GROUP BY f.id, gf.id_genre, fd.director_id " +
                "ORDER BY f.rate " +
                "LIMIT ?";
        final String sql = commonSQLPartForReading + where;

        return jdbcTemplate.query(sql, mapperGetFilms(), year, limit).stream()
                .findFirst()
                .orElse(Collections.emptyList());
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        log.debug("Выполняем getCommonFilms({}, {})", userId, friendId);
        String sql = "SELECT f.id, name, description, release_date, duration, mpa, COUNT(lf.id_user) AS rate, " +
                "gf.id_genre, g.name_genre, fd.director_id, d.director_name " +
                "FROM LIKE_FILM lf " +
                "JOIN LIKE_FILM lf2 ON lf2.ID_USER = ? AND lf.ID_FILM = lf2.ID_FILM " +
                "JOIN FILM f ON f.id = lf.ID_FILM " +
                "LEFT JOIN genre_film AS gf ON f.id = gf.id_film " +
                "LEFT JOIN genre AS g ON gf.id_genre = g.id_genre " +
                "LEFT JOIN films_directors AS fd ON f.id = fd.film_id " +
                "LEFT JOIN directors AS d ON d.director_id = fd.director_id " +
                "WHERE lf.ID_USER = ? " +
                "GROUP BY f.id, gf.id_genre, fd.director_id " +
                "ORDER BY f.rate DESC";

        return jdbcTemplate.query(sql, mapperGetFilms(), userId, friendId).stream()
                .findFirst()
                .orElse(Collections.emptyList());
    }

    public List<Film> getRecommendedFilmsForUser(int userId) {
        String filmIdArray =
                "SELECT id_film FROM like_film " +
                        "WHERE id_user = ( " +
                        "SELECT id_user FROM like_film " +
                        "WHERE id_film IN ( " +
                        "SELECT id_film FROM like_film " +
                        "WHERE id_user = ? " +
                        ") " +
                        "AND id_user <> ? " +
                        "GROUP BY id_user  " +
                        "ORDER BY COUNT(id_user) DESC " +
                        "LIMIT 1 " +
                        ") " +
                        "EXCEPT ( " +
                        "SELECT id_film FROM like_film " +
                        "WHERE id_user = ? " +
                        ")";
        String sql = commonSQLPartForReading +
                "WHERE f.id IN (" + filmIdArray +
                ")" +
                "GROUP BY f.id, gf.id_genre, fd.director_id " +
                "ORDER BY f.rate DESC";

        return jdbcTemplate.query(sql, mapperGetFilms(), userId, userId, userId).stream()
                .findFirst()
                .orElse(Collections.emptyList());
    }

    @Override
    public Film findFilmById(int id) {
        log.debug("Выполняем findFilmById({}})", id);
        return jdbcTemplate.queryForObject(commonSQLPartForReading +
                "WHERE f.id =? " +
                "GROUP BY gf.id_genre", filmRowMapper(), id);
    }

    @Override
    public Film save(Film film) {
        log.debug("Выполняем save(Film film)");
        SimpleJdbcInsert filmInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> filmParams = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa", film.getMpa().getId());
        Number id = filmInsert.executeAndReturnKey(filmParams);

        List<Map<String, Object>> genreParamsList = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            Map<String, Object> genreParams = Map.of(
                    "id_genre", genre.getId(),
                    "id_film", id);
            genreParamsList.add(genreParams);
        }

        SimpleJdbcInsert genreFilmInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("GENRE_FILM");
        genreFilmInsert.executeBatch(genreParamsList.toArray(new Map[0]));

        insertIntoFilmsDirectors(id.intValue(), film);
        film.setId(id.intValue());
        return findFilmById(id.intValue());
    }


    @Override
    public Film update(Film film) {
        Integer id = film.getId();
        Integer countLikes = getCountOfLikesForFilm(id);
        updateFilmData(film, countLikes);
        updateGenreFilmRelationships(id, new ArrayList<>(film.getGenres()));
        updateFilmsDirectorsRelationship(film);
        return findFilmById(id);
    }

    private Integer getCountOfLikesForFilm(Integer filmId) {
        try {
            return jdbcTemplate.queryForObject("SELECT count(*) AS count FROM LIKE_FILM WHERE id_film = ?",
                    (rs, rowNum) -> rs.getInt("count"), filmId);
        } catch (RuntimeException e) {
            return 0;
        }
    }

    private void updateFilmData(Film film, Integer countLikes) {
        jdbcTemplate.update("UPDATE film SET name=?, description=?, release_date=?, " +
                        "duration=?, mpa=?, rate=? " +
                        "WHERE id=?", film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), countLikes, film.getId());
    }

    private void updateGenreFilmRelationships(Integer filmId, List<Genre> genres) {
        jdbcTemplate.update("DELETE FROM genre_film WHERE id_film=?", filmId);
        SimpleJdbcInsert genreFilmInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("genre_film");
        if (!genres.isEmpty()) {
            List<Map<String, Object>> genreParamsList = new ArrayList<>();
            for (Genre genre : genres) {
                Map<String, Object> params = Map.of(
                        "id_genre", genre.getId(),
                        "id_film", filmId);
                if (!genreParamsList.contains(params)) {
                    genreParamsList.add(params);
                }
            }
            genreFilmInsert.executeBatch(genreParamsList.toArray(new Map[0]));
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM FILM WHERE id =?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Map<Integer, Film> getStorageFilm() {
        Map<Integer, Film> filmMap = new HashMap<>();
        jdbcTemplate.query("SELECT * FROM film", rs -> {
            do {
                int idFilm = rs.getInt("id");
                if (!filmMap.containsKey(idFilm)) {
                    Film film = mapperGetStorageFilm(rs);
                    filmMap.put(film.getId(), film);
                }
            } while (rs.next());
        });
        return filmMap;
    }

    @Override
    public void likeForFilm(int filmId, int userId) {
        addLikeForFilm(filmId, userId);
        updateRateForFilm(filmId);
    }

    public void addLikeForFilm(int filmId, int userId) {
        String insertSql = "INSERT INTO LIKE_FILM (id_film, id_user) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, filmId, userId);
    }

    public void updateRateForFilm(int filmId) {
        String updateSql = "UPDATE FILM SET rate = rate + 1 WHERE id = ?";
        jdbcTemplate.update(updateSql, filmId);
    }

    @Override
    public void deleteLikeForFilm(int id, int userId) {
        String sql = "DELETE FROM LIKE_FILM WHERE id_film = ? AND ID_USER = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT id " +
                "FROM film " +
                "WHERE id = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExist;
    }

    @Override
    public boolean isExistLike(int filmId, int userId) {
        HashMap<Integer, Integer> likeMap = new HashMap<>();
        String sql = "SELECT * FROM LIKE_FILM lf WHERE lf.ID_FILM = ? AND lf.ID_USER = ?";
        jdbcTemplate.query(sql, (rs) -> {
            Integer fetchedFilmId = rs.getInt("ID_FILM");
            Integer fetchedUserId = rs.getInt("ID_USER");
            if (fetchedFilmId != null && fetchedUserId != null && fetchedFilmId > 0 && fetchedUserId > 0) {
                likeMap.put(fetchedFilmId, fetchedUserId);
            }
        }, filmId, userId);

        if (likeMap.containsKey(filmId)) {
            return !likeMap.get(filmId).equals(userId);
        } else {
            return true;
        }
    }

    private void insertIntoFilmsDirectors(int filmId, Film film) {
        if (film.getDirectors().isEmpty()) {
            return;
        }

        List<Map<String, Object>> filmsDirectorsInsertion = new ArrayList<>();
        for (Director director : film.getDirectors()) {
            Map<String, Object> entitiesIdMap = Map.of(
                    "film_id", filmId,
                    "director_id", director.getId());
            filmsDirectorsInsertion.add(entitiesIdMap);
        }

        SimpleJdbcInsert filmsDirectorsInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("FILMS_DIRECTORS");
        filmsDirectorsInsert.executeBatch(filmsDirectorsInsertion.toArray(new Map[0]));
    }

    private void insertIntoFilmsDirectors(Film film) {
        insertIntoFilmsDirectors(film.getId(), film);
    }

    private void updateFilmsDirectorsRelationship(Film film) {
        jdbcTemplate.update("DELETE FROM films_directors WHERE film_id = ?", film.getId());
        insertIntoFilmsDirectors(film);
    }

    private String wrapInPercent(String str) {
        return "%" + str + "%";
    }
}

