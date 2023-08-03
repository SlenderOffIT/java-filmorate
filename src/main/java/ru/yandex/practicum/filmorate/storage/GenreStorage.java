package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public class GenreStorage {

    JdbcTemplate jdbcTemplate;

    public GenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE g", (rs, rowNum) -> {
            return new Genre(rs.getInt("id_genre"), rs.getString("name_genre"));
        });
    }

    public Genre getGenreById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM GENRE WHERE ID_GENRE = ?", (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id_genre"));
            genre.setName(rs.getString("name_genre"));
            return genre;
            }, id);
    }

    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT id_genre FROM GENRE g WHERE ID_GENRE = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExist;
    }
}
