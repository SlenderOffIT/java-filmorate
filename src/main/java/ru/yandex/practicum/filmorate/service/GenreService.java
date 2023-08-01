package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Slf4j
@Service
public class GenreService {

    JdbcTemplate jdbcTemplate;

    public GenreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        log.debug("Поступил запрос на просмотр всех жанров фильмов.");
        return jdbcTemplate.query("SELECT * FROM GENRE g", (rs, rowNum) -> {
            return new Genre(rs.getInt("id_genre"), rs.getString("name_genre"));
        });
    }

    public Genre getGenreById(int id) {
        log.debug("Поступил запрос на просмотр фильма с id {}.", id);
        if (isExist(id)) {
            return jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE ID_GENRE = ?", (rs, rowNum) -> {
                Genre genre = new Genre();
                genre.setId(rs.getInt("id_genre"));
                genre.setName(rs.getString("name_genre"));
                return genre;
            }, id);
        } else {
            log.debug("Жанра с таким id не существующего {}", id);
            throw new GenreNotFoundException(String.format("Жанра с таким id %d не существует.", id));
        }
    }

    public boolean isExist(int id) {
        String sql = "SELECT count(ID_GENRE) FROM GENRE g WHERE ID_GENRE = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (count < 1) {
            return false;
        } else {
            return true;
        }
    }
}
