package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

@Component
public class RatingMpaStorage {

    JdbcTemplate jdbcTemplate;

    public RatingMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RatingMpa> getAllRatingMpa() {
        return jdbcTemplate.query("SELECT * FROM RATING_MPA", (rs, rowNum) ->
                new RatingMpa(rs.getInt("id_rating_mpa")));
    }

    public RatingMpa getRantingMpaById(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM rating_mpa WHERE id_rating_mpa = ?", (rs, rowNum) -> {
            RatingMpa ratingMpa = new RatingMpa();
            ratingMpa.setId(rs.getInt("id_rating_mpa"));
            ratingMpa.setName(rs.getString("name_mpa"));
            return ratingMpa;
            }, id);
    }

    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT ID_RATING_MPA FROM RATING_MPA WHERE ID_RATING_MPA = ?)";
        boolean isExist = jdbcTemplate.queryForObject(sql, Boolean.class, id);
        return isExist;
    }
}
