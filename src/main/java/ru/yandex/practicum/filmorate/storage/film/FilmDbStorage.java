package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

import static ru.yandex.practicum.filmorate.storage.Mapping.*;

@Component
public class FilmDbStorage implements FilmStorage {
    JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query("SELECT f.id, name, description, release_date, duration, mpa, " +
                "COUNT(lf.id_film) AS rate, gf.id_genre, g.name_genre " +
                "FROM film as f " +
                "LEFT JOIN like_film AS lf ON f.id = lf.id_film " +
                "LEFT JOIN genre_film AS gf ON f.id = gf.id_film " +
                "LEFT JOIN genre AS g ON gf.id_genre = g.id_genre " +
                "GROUP BY f.id, gf.id_genre " +
                "ORDER BY f.id, id_genre", mapperGetFilms()).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public Film findFilmById(int id) {
        return jdbcTemplate.queryForObject("SELECT f.id, name, description, release_date, duration, mpa, " +
                "COUNT(lf.id_film) AS rate, gf.id_genre, g.name_genre " +
                "FROM film as f " +
                "LEFT JOIN like_film AS lf ON f.id = lf.id_film " +
                "LEFT JOIN genre_film AS gf ON f.id = gf.id_film " +
                "LEFT JOIN genre AS g ON gf.id_genre = g.id_genre " +
                "WHERE f.id =? " +
                "GROUP BY gf.id_genre " +
                "ORDER BY id_genre", filmRowMapper(), id);
    }

    @Override
    public Film save(Film film) {
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

        film.setId(id.intValue());

        return film;
    }


    @Override
    public Film update(Film film) {
        Integer id = film.getId();
        Integer countLikes = getCountOfLikesForFilm(id);
        updateFilmData(film, countLikes);
        updateGenreFilmRelationships(id, film.getGenres());
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
                } else {
                    continue;
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
            if (likeMap.get(filmId).equals(userId)) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
