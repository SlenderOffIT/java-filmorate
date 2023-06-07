package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;
import java.util.List;

/**
 * Данный контроллер содержит в себе эндпоинты взаимодействия с фильмами:
 * /films - для просмотра, добавления и редактирования фильма;
 * /films/{id} - для просмотра и удаления фильма;
 * /films/popular - для просмотра топа фильмов, defaultValue = 10;
 * /films/{id}/like/{userId} - для добавления и удаления лайков фильму.
 */
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    InMemoryFilmStorage inMemoryFilmStorage;
    FilmService filmService;

    @Autowired
    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    @GetMapping("/{id}")
    public Film watchFilmById(@PathVariable int id) {
        return inMemoryFilmStorage.watchFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> topFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.topFilms(count);
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        return inMemoryFilmStorage.postFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        return inMemoryFilmStorage.putFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Integer likeForFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.likeForFilm(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        inMemoryFilmStorage.deleteFilm(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Integer deleteLikeForFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.deleteLikeForFilm(id, userId);
    }
}
