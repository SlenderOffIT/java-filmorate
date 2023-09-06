package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.ratingMpa.RatingMpaServiceImpl;

import java.util.List;

/**
 * Контроллер рейтинга MPA:
 * /mpa - просмотр всех рейтингов;
 * /mpa/{id} - просмотр рейтинга по id.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/mpa")
public class RatingMpaController {

    RatingMpaServiceImpl ratingMpaService;

    @GetMapping
    public List<RatingMpa> getAllRatingMpa() {
        log.debug("Поступил запрос на просмотр всех рейтингов.");
        return ratingMpaService.getAllRatingMpa();
    }

    @GetMapping("/{id}")
    public RatingMpa getRantingMpaById(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр рейтинга с id {}.", id);
        return ratingMpaService.getRantingMpaById(id);
    }
}
