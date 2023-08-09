package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

/**
 * Контроллер фильмов:
 * /films - просмотр, добавление и редактирование фильма;
 * /films/{id} - просмотр и удаление фильма;
 * /films/popular - просмотр топа фильмов, defaultValue = 10;
 * /films/{id}/like/{userId} - добавление и удаление лайков фильму.
 */
@Slf4j
@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    FilmService filmService;

    @GetMapping
    public Collection<Film> getFilms() {
        log.debug("Поступил запрос на просмотр всех фильмов.");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр фильма с id {}.", id);
        return filmService.findFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> topFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.debug("Поступил запрос на просмотр топ {} фильмов.", count);
        return filmService.topFilms(count);
    }

    @PostMapping
    public Film postFilm(@RequestBody Film film) {
        log.debug("Поступил запрос на добавление фильма с id {}.", film.getId());
        return filmService.postFilm(film);
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        log.debug("Поступил запрос на изменение фильма с id {}.", film.getId());
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeForFilm(@PathVariable int id, @PathVariable int userId) {
        log.debug("Поступил запрос на добавление лайка фильму с id {} от пользователя с id {}.", id, userId);
        filmService.likeForFilm(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable int id) {
        log.debug("Поступил запрос на удаление фильма с id {}.", id);
        filmService.delete(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeForFilm(@PathVariable int id, @PathVariable int userId) {
        log.debug("Поступил запрос на удаление лайка фильму с id {} от пользователя с id {}.", id, userId);
        filmService.deleteLikeForFilm(id, userId);
    }
}