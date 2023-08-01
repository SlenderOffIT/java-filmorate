package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

@Slf4j
@Service
public class RatingMpaService {

    JdbcTemplate jdbcTemplate;

    public RatingMpaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RatingMpa> getAllRatingMpa() {
        return jdbcTemplate.query("SELECT * FROM RATING_MPA", (rs, rowNum) ->
                new RatingMpa(rs.getInt("id_rating_mpa")));
    }

    public RatingMpa getRantingMpaById(int id) {
        if (isExist(id)) {
            return jdbcTemplate.queryForObject("SELECT * FROM rating_mpa WHERE id_rating_mpa = ?", (rs, rowNum) -> {
                RatingMpa ratingMpa = new RatingMpa();
                ratingMpa.setId(rs.getInt("id_rating_mpa"));
                ratingMpa.setName(rs.getString("name_mpa"));
                return ratingMpa;
            }, id);
        } else {
            log.debug("Рейтинг MPA с таким id не существующего {}", id);
            throw new MpaNotFoundException(String.format("Рейтинг MPA с таким id %d не существует.", id));
        }
    }

    public boolean isExist(int id) {
        String sql = "SELECT count(ID_RATING_MPA) FROM RATING_MPA WHERE ID_RATING_MPA = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (count < 1) {
            return false;
        } else {
            return true;
        }
    }
}
