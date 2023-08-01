package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.Validations.validation;

/**
 * Класс FilmService выполняет:
 * likeForFilm - добавлением лайка для фильма;
 * deleteLikeForFilm - удалением лайка у фильма;
 * topFilms - выводит список топовых фильмов по лайкам.
 */
@Slf4j
@Service
@AllArgsConstructor
public class FilmService {

    FilmStorage filmStorage;
    UserStorage userStorage;

    public Collection<Film> getFilms() {
        log.debug("Поступил запрос на просмотр всех имеющихся фильмов.");
        return filmStorage.getFilms();
    }

    public Film findFilmById(int id) {
        log.debug("Поступил запрос на просмотр данных фильма с id {}", id);
        if (!filmStorage.isExist(id)) {
            log.debug("Фильма с таким id не существующего {}", id);
            throw new FilmNotFoundException(String.format("Фильма с id %d в нашей базе нету", id));
        }
        return filmStorage.findFilmById(id);
    }

    public Film postFilm(Film film) {
        log.debug("Поступил запрос на добавление фильма с id {}", film.getId());
        validation(film);
        return filmStorage.postFilm(film);
    }

    public Film update(Film film) {
        log.debug("Поступил запрос на изменение данных фильма с id {}", film.getId());
        if (!filmStorage.isExist(film.getId())) {
            log.debug("Фильма с таким id {} не существует.", film.getId());
            throw new ValidationException("Фильма с таким id " + film.getId() + " не существует.");
        }
        validation(film);
        return filmStorage.update(film);
    }

    public void delete(int id) {
        log.debug("Поступил запрос на удаление фильма с id {}", id);
        if (!filmStorage.isExist(id)) {
            log.debug("Поступил запрос на удаление не существующего фильма с id {}", id);
            throw new FilmNotFoundException(String.format("Фильма с id %d не существует", id));
        }
        filmStorage.delete(id);
    }

    public Integer likeForFilm(int id, int userId) {
        if (!userStorage.isExist(userId)) {
            log.debug("Поступил запрос на добавление лайка от несуществующего пользователя с id {}", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован", userId));
        }
        if (!filmStorage.isExist(id)) {
            log.debug("Поступил запрос на добавление лайка у несуществующего фильма с id {}", id);
            throw new UserNotFoundException(String.format("Фильм с id %d не не существует", id));
        }
        Film film = filmStorage.getStorageFilm().get(id);
        if (!film.getFilmLikes().contains(userId)) {
            film.getFilmLikes().add(userId);
            film.incrementRating();
            filmStorage.update(film);
            log.debug("Пользователь с id {} поставил лайк фильму с id {}", userId, id);
        }
        return film.getRating();
    }

    public Integer deleteLikeForFilm(int id, int userId) {
        if (!userStorage.isExist(userId)) {
            log.debug("Поступил запрос на удаление лайка от несуществующего пользователя с id {}", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован", userId));
        }
        if (!filmStorage.isExist(id)) {
            log.debug("Поступил запрос на удаление лайка у несуществующего фильма с id {}", id);
            throw new UserNotFoundException(String.format("Фильм с id %d не не существует", id));
        }
        Film film = filmStorage.getStorageFilm().get(id);
        if (film.getFilmLikes().contains(userId)) {
            film.getFilmLikes().remove(userId);
            film.decrementRating();
            filmStorage.update(film);
            log.debug("Пользователь с id {} удалил лайк с фильма с id {}", userId, id);
        }
        return film.getRating();
    }

    /**
     * @param count  обозначает какой топ по количеству лайков нужно найти в списке фильмов,
     *              если данное значение не указано, то вернется список из первых 10 фильмов
     * @return вернет нужный список фильмов
     * В методе так же сортируем (.sort) фильмы по количеству лайков и выводим в обратном порядке,
     * от большего к меньшему и если переменная count заданна, то устанавливаем limit на данное значение
     * и собираем (collect) все элементы стрима в список.
     */
    public List<Film> topFilms(Integer count) {
        log.debug("Пользователь запросил топ {} фильмов", count);
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getFilmLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}