package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.util.FilmSortingCriteria.FilmSortingCriteria;

import java.util.Collection;
import java.util.List;


/**
 * Контроллер фильмов:
 * /films - просмотр, добавление и редактирование фильма;
 * /films/{id} - просмотр и удаление фильма;
 * /films/popular - просмотр топа фильмов, defaultValue = 10;
 * /films/{id}/like/{userId} - добавление и удаление лайков фильму.
 * /films/director/{directorId}?sortBy=[year,likes] - получение списка фильмов конкретного режиссера,
 * отсортированного по годам/популярности.
 * /films/search/?query=str&by=title,director - получение списка фильмов по параметрам поиска
 * отсортированного по популярности.
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
    public List<Film> topFilms(@RequestParam(defaultValue = "10") Integer count,
                               @RequestParam(required = false) Integer genreId,
                               @RequestParam(required = false) Integer year) {
        if (genreId != null && year != null) {
            log.debug("Поступил запрос на получение списка {} самых популярных фильмов за {} год с id жанра {}.",
                    count, year, genreId);
        } else if (genreId != null) {
            log.debug("Поступил запрос на получение списка {} самых популярных фильмов с id жанра {}.",
                    count, genreId);
        } else if (year != null) {
            log.debug("Поступил запрос на получение списка {} самых популярных фильмов за {} год.",
                    count, year);
        } else {
            log.debug("Поступил запрос на просмотр топ {} фильмов.", count);
        }

        return filmService.topFilms(count, genreId, year);
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

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmsOfDirector(@PathVariable int directorId,
                                               @RequestParam("sortBy") FilmSortingCriteria criteria) {
        log.debug("Поступил запрос на получение фильмов режиссера с id {} по критерию {}.",
                directorId, criteria.name());
        return filmService.getSortedFilmsOfDirector(directorId, criteria);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam("query") String query,
                                  @RequestParam("by") String by) {
        log.debug("Поступил запрос на получение списка фильмов по поиску с параметрами: название {} по {}.",
                query, by);
        if (!by.contains("title") && !by.contains("director")) {
            throw new IncorrectParameterException("by");
        }
        return filmService.searchFilms(query, by);
    }
}