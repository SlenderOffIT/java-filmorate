package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Mapping {

    public static RowMapper<List<Film>> mapperGetFilms() {
        return (rs, rowNum) -> {
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
                    mapGenres(rs, film);
                    mapDirectors(rs, film);
                } else {
                    film = new Film();
                    film.setId(rs.getInt("id"));
                    film.setName(rs.getString("name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));
                    film.setMpa(new RatingMpa(rs.getInt("mpa")));
                    film.setRate(rs.getInt("rate"));
                    mapGenres(rs, film);
                    mapDirectors(rs, film);

                }
                if (!films.stream()
                        .map(Film::getId)
                        .collect(Collectors.toList()).contains(film.getId())) {
                    films.add(film);
                }
            } while (rs.next());
            return films;
        };
    }

    public static RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getInt("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new RatingMpa(rs.getInt("mpa")));
            film.setRate(rs.getInt("rate"));
            do {
                mapDirectors(rs, film);
                mapGenres(rs, film);
            } while (rs.next());

            return film;
        };
    }

    private static void mapDirectors(ResultSet rs, Film film) throws SQLException {
        if (rs.getInt("director_id") == 0) {
            return;
        }
        film.getDirectors().add(new Director(rs.getInt("director_id"),
                rs.getString("director_name")));
    }

    private static void mapGenres(ResultSet rs, Film film) throws SQLException {
        if (rs.getInt("id_genre") > 0) {
            film.getGenres().add(new Genre(rs.getInt("id_genre"),
                    rs.getString("name_genre")));
        }
    }

    public static Film mapperGetStorageFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        RatingMpa ratingMpa = new RatingMpa(rs.getInt("mpa"));
        // Здесь также можно добавить логику для установки остальных свойств объекта Film

        return film;
    }

    public static RowMapper<User> mapperGetUser() {
        return (rs, rowNum) -> {
            int userId = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            String login = rs.getString("login");
            LocalDate birthday = rs.getDate("birthday").toLocalDate();

            User user = new User(userId, email, login, name, birthday);
            do {
                int friend = rs.getInt("id_friend");
                user.setListFriends(friend);
            } while (rs.next());
            return user;
        };
    }

    public static RowMapper<List<User>> listUserRowMapper() {
        return ((rs, rowNum) -> {
            List<User> users = new ArrayList<>();

            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());

            do {
                if (user.getId() == rs.getInt("id")) {
                    if (rs.getInt("id_friend") > 0) {
                        user.setListFriends(rs.getInt("id_friend"));
                    }
                } else {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setLogin(rs.getString("login"));
                    user.setBirthday(rs.getDate("birthday").toLocalDate());
                    if (rs.getInt("id_friend") > 0) {
                        user.setListFriends(rs.getInt("id_friend"));
                    }
                }
                if (!users.stream()
                        .map(User::getId)
                        .collect(Collectors.toList()).contains(user.getId())) {
                    users.add(user);
                }
            } while (rs.next());
            return users;
        });
    }

    public static RowMapper<User> mapperListMutualFriends() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        };
    }
}
