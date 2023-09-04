package ru.yandex.practicum.filmorate.service.ratingMpa;

import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;

public interface RatingMpaService {

    List<RatingMpa> getAllRatingMpa();
    RatingMpa getRantingMpaById(int id);
}
