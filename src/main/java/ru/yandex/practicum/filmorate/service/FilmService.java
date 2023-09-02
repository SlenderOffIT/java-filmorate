package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExists.LikeAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.NotFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFound.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.FilmSortingCriteria.FilmSortingCriteria;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.feed.FeedDbStorage.*;
import static ru.yandex.practicum.filmorate.util.Validations.validation;

/**
 * Класс FilmService выполняет:
 * likeForFilm - добавлением лайка для фильма;
 * deleteLikeForFilm - удалением лайка у фильма;
 * getSortedFilmsOfDirector - возвращает список фильмов режиссера с определенным id,
 * отсортированных по заданному критерию;
 * topFilms - выводит список топовых фильмов по лайкам.
 * searchFilms - ывводит список фильмов, отсортированных по популярности, по параметрам поиска
 */
@Slf4j
@Service
public class FilmService {

    FilmStorage filmStorage;
    UserStorage userStorage;
    FeedStorage feedStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("feedDbStorage") FeedStorage feedStorage) {  // Добавляем инициализацию в конструктор
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;  // Инициализируем feedStorage
    }

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
        return filmStorage.save(film);
    }

    public Film update(Film film) {
        log.debug("Поступил запрос на изменение данных фильма с id {}", film.getId());
        if (!filmStorage.isExist(film.getId())) {
            log.debug("Фильма с таким id {} не существует.", film.getId());
            throw new FilmNotFoundException("Фильма с таким id " + film.getId() + " не существует.");
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

    public void likeForFilm(int id, int userId) {
        if (!userStorage.isExist(userId)) {
            log.debug("Поступил запрос на добавление лайка от несуществующего пользователя с id {}.", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован.", userId));
        }
        if (!filmStorage.isExist(id)) {
            log.debug("Поступил запрос на добавление лайка у несуществующего фильма с id {}.", id);
            throw new FilmNotFoundException(String.format("Фильм с id %d не не существует.", id));
        }
        if (!filmStorage.isExistLike(id, userId)) {
            log.debug("Повторный лайк от пользователя с id {}.", userId);
            throw new LikeAlreadyExistsException(String.format("Лайк от пользователя %d уже есть.", userId));
        }
        filmStorage.likeForFilm(id, userId);
        feedStorage.addFeed(userId, LIKE, ADD, id);  // Добавляем событие в ленту

    }

    public void deleteLikeForFilm(int id, int userId) {
        if (!userStorage.isExist(userId)) {
            log.debug("Поступил запрос на удаление лайка от несуществующего пользователя с id {}", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован", userId));
        }
        if (!filmStorage.isExist(id)) {
            log.debug("Поступил запрос на удаление лайка у несуществующего фильма с id {}", id);
            throw new FilmNotFoundException(String.format("Фильм с id %d не не существует", id));
        }
        if (filmStorage.isExistLike(id, userId)) {
            log.debug("Лайка от пользователя с id {} нету.", userId);
            throw new LikeNotFoundException(String.format("Лайк от пользователя %d нету.", userId));
        }
        filmStorage.deleteLikeForFilm(id, userId);
        feedStorage.addFeed(userId, LIKE, REMOVE, id);  // Добавляем событие об удалении лайка в ленту

    }

    /**
     * @param count обозначает какой топ по количеству лайков нужно найти в списке фильмов,
     *              если данное значение не указано, то вернется список из первых 10 фильмов
     * @return вернет нужный список фильмов
     * В методе сортируем (.sort) фильмы по количеству лайков и выводим в обратном порядке,
     * если переменная count заданна, то устанавливаем limit на данное значение
     * и собираем (collect) все элементы стрима в список.
     */
    public List<Film> topFilms(Integer count, Integer genreId, Integer year) {
        if (genreId != null && year != null) {
            log.debug("Пользователь запросил список {} самых популярных фильмов за {} год с id жанра {}.",
                    count, year, genreId);
            return filmStorage.popularGenreYearSearch(genreId, year, count);
        }

        if (genreId != null) {
            log.debug("Пользователь запросил список {} самых популярных фильмов с id жанра {}.",
                    count, genreId);
            return filmStorage.popularGenreSearch(genreId, count);
        }

        if (year != null) {
            log.debug("Пользователь запросил список {} самых популярных фильмов за {} год.",
                    count, year);
            return filmStorage.popularYearSearch(year, count);
        }

        log.debug("Пользователь запросил топ {} фильмов", count);
        return getFilms().stream()
                .sorted(Comparator.comparingInt(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getSortedFilmsOfDirector(int directorId,
                                               FilmSortingCriteria criteria) {
        log.debug("Пользователь запросил список фильмов режиссера с id = {}, отсортированных по критерию {}",
                directorId, criteria.name());
        return filmStorage.getSortedFilmsOfDirector(directorId, criteria);
    }

    public List<Film> searchFilms(String query, String by) {
        log.debug("Пользователь запросил список фильмов по поиску с параметрами: название {} и по {}.",
                query, by);
        return filmStorage.searchFilms(query, by);
    }
}
