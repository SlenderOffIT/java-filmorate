package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Component
public class DirectorStorage {

    JdbcTemplate jdbcTemplate;

    public DirectorStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Director> getAllDirectors() {
        return jdbcTemplate.query("SELECT * FROM director", (rs, rowNum) ->
                new Director(rs.getInt("id_director"), rs.getString("name_director")));
    }

    public Director getDirectorById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM director WHERE id_director = ?", (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getInt("id_director"));
            director.setName(rs.getString("name_director"));
            return director;
        }, id);
    }

    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT id_director FROM director WHERE id_director = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExist;
    }
}