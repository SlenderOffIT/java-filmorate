package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

/**
 * Контроллер жанров:
 * /genres - просмотр всех жанров;
 * /genres/{id} - просмотр жанра по id.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/genres")
public class GenreController {

    GenreService genreService;

    @GetMapping
    public List<Genre> getAllGenres() {
        log.debug("Поступил запрос на просмотр всех жанров.");
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр жанра с id {}.", id);
        return genreService.getGenreById(id);
    }
}