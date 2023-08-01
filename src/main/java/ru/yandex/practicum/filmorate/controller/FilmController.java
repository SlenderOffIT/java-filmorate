package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

/**
 * Данный контроллер содержит в себе эндпоинты взаимодействия с фильмами:
 * /films - для просмотра, добавления и редактирования фильма;
 * /films/{id} - для просмотра и удаления фильма;
 * /films/popular - для просмотра топа фильмов, defaultValue = 10;
 * /films/{id}/like/{userId} - для добавления и удаления лайков фильму.
 */
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    FilmService filmService;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable int id) {
        return filmService.findFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> topFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.topFilms(count);
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        return filmService.postFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeForFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.likeForFilm(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        filmService.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeForFilm(@PathVariable int id, @PathVariable int userId) {
        filmService.deleteLikeForFilm(id, userId);
    }
}