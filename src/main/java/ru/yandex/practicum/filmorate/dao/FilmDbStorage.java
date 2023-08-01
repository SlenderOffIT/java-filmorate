package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("filmDbStorage")
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
                "ORDER BY f.id, id_genre", (rs, rowNum) -> {
                    List<Film> films = new ArrayList<>();

                    if (rs.wasNull()) {
                        return films;
                    }

                    Film film = new Film();
                    film.setId(rs.getInt("id"));
                    film.setName(rs.getString("name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));
                    film.setMpa(new RatingMpa(rs.getInt("mpa")));
                    film.setRate(rs.getInt("rate"));

                    do {
                        if (film.getId() == rs.getInt("id")) {
                            if (rs.getInt("id_genre") > 0) {
                                film.getGenres().add(new Genre(rs.getInt("id_genre"), rs.getString("name_genre")));
                            }
                        } else {
                            film = new Film();
                            film.setId(rs.getInt("id"));
                            film.setName(rs.getString("name"));
                            film.setDescription(rs.getString("description"));
                            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                            film.setDuration(rs.getInt("duration"));
                            film.setMpa(new RatingMpa(rs.getInt("mpa")));
                            film.setRate(rs.getInt("rate"));
                            if (rs.getInt("id_genre") > 0) {
                                film.getGenres().add(new Genre(rs.getInt("id_genre"), rs.getString("name_genre")));
                            }
                        }
                        if (!films.stream()
                                .map(Film::getId)
                                .collect(Collectors.toList()).contains(film.getId())) {
                            films.add(film);
                        }
                    } while (rs.next());
                    return films;
                }).stream().findFirst().orElse(new ArrayList<>());
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
                "ORDER BY id_genre", (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new RatingMpa(rs.getInt("mpa")));
            film.setRate(rs.getInt("rate"));
            if (rs.getInt("id_genre") > 0) {
                do {
                    film.getGenres().add(new Genre(rs.getInt("id_genre"), rs.getString("name_genre")));
                } while (rs.next());
            }
            return film;
        }, id);
    }

    @Override
    public Film postFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("film")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa", film.getMpa().getId());
        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName("GENRE_FILM");
        Set<Genre> genres = new HashSet<>(film.getGenres());
        film.getGenres().clear();
        if (genres.size() > 0) {
            for (Genre genre : genres) {
                film.getGenres().add(genre);
                params = Map.of(
                        "id_genre", genre.getId(),
                        "id_film", id);
                simpleJdbcInsert.execute(params);
            }
        }
        film.setId(id.intValue());

        return film;
    }

    @Override
    public Film update(Film film) {
        Integer id = film.getId();
        Integer countLikes;
        try {
            countLikes = jdbcTemplate.queryForObject("SELECT count(*) AS count FROM LIKE_FILM where id_film = ?",
                    (rs, rowNum) -> rs.getInt("count"), film.getId());
        } catch (RuntimeException e) {
            countLikes = 0;
        }
        jdbcTemplate.update("UPDATE film SET  name=?, description=?, release_date=?, " +
                        "duration=?, mpa=?, rate=? " +
                        "WHERE id=?", film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), countLikes, film.getId());

        jdbcTemplate.update("DELETE FROM genre_film WHERE id_film=?", id);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("genre_film");
        Set<Genre> genres = new HashSet<>(film.getGenres());
        film.getGenres().clear();
        if (genres.size() > 0) {
            for (Genre genre : genres) {
                Map<String, Object> params = Map.of(
                        "id_genre", genre.getId(),
                        "id_film", id);
                simpleJdbcInsert.execute(params);
            }
        }
        return findFilmById(id);
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
                    Film film = new Film();
                    film.setId(rs.getInt("id"));
                    film.setName(rs.getString("name"));
                    film.setDescription(rs.getString("description"));
                    film.setDuration(rs.getInt("duration"));
                    film.setRate(rs.getInt("rate"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    RatingMpa ratingMpa = new RatingMpa(rs.getInt("mpa"));
                    filmMap.put(film.getId(), film);
                }
            } while (rs.next());
        });
        return filmMap;
    }

    @Override
    public void likeForFilm(int filmId, int userId) {
        String insertSql = "INSERT INTO LIKE_FILM (id_film, id_user) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, filmId, userId);

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
        String sql = "SELECT COUNT(id) " +
                "FROM film " +
                "WHERE id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (count < 1) {
            return false;
        } else {
            return true;
        }
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
