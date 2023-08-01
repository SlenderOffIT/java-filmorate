package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/mpa")
public class RatingMpaController {

    RatingMpaService ratingMpaService;

    @GetMapping
    public List<RatingMpa> getAllRatingMpa() {
        return ratingMpaService.getAllRatingMpa();
    }

    @GetMapping("/{id}")
    public RatingMpa getRantingMpaById(@PathVariable int id) {
        return ratingMpaService.getRantingMpaById(id);
    }
}
