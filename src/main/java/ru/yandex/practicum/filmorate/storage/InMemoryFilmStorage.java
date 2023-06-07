package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Validations.validation;

/**
 * Данный класс InMemoryFilmStorage предназначен для хранения, добавления, обновления, удаления
 * и поиска пользователей нашего сервиса.
 * getFilms - просмотр всех имеющихся фильмов;
 * watchFilmById - просмотр фильма по id;
 * postFilm - добавление фильма;
 * putFilm - обновление данных о фильме;
 * deleteFilm - удаление фильма.
 */
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private int id = 1;
    @Getter
    private final Map<Integer, Film> storageFilm = new HashMap<>();

    public Collection<Film> getFilms() {
        log.info("Поступил запрос на просмотр всех имеющихся фильмов.");
        return storageFilm.values();
    }

    public Film watchFilmById(int id) {
        if (!storageFilm.containsKey(id)) {
            log.debug("Поступил запрос на просмотр не существующего фильма с id {}", id);
            throw new FilmNotFoundException(String.format("Фильма с id %d в нашей базе нету", id));
        }
        log.info("Поступил запрос на просмотр данных фильма с id {}", id);
        return storageFilm.get(id);
    }

    public Film postFilm(Film film) {
        validation(film);
        film.setId(id++);
        storageFilm.put(film.getId(), film);
        log.info("Добавили фильм {}", film.getName());
        return film;
    }

    public Film putFilm(Film film) {
        if (storageFilm.containsKey(film.getId())) {
            validation(film);
            storageFilm.put(film.getId(), film);
            log.info("Изменили данные о фильме {}", film.getName());
        } else {
            throw new ValidationException("Фильма с таким id " + film.getId() + " не существует.");
        }
        return film;
    }

    public void deleteFilm(int id) {
        log.info("Поступил запрос на удаление фильма с id {}", id);
        storageFilm.remove(id);
    }
}
