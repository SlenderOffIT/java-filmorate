package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.ValidationFilm.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private int id = 1;
    @Getter
    private final Map<Integer, Film> storageFilm = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("Просмотрены все фильмы");
        return storageFilm.values();
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        validation(film);
        film.setId(id++);
        storageFilm.put(film.getId(), film);
        log.info("Добавили фильм " + film.getName());
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        if (storageFilm.containsKey(film.getId())) {
            validation(film);
            storageFilm.put(film.getId(), film);
            log.info("Изменили данные о фильме " + film.getName());
        } else {
            throw new ValidationException("Фильма с таким id " + film.getId() + " не существует.");
        }
        return film;
    }
}
