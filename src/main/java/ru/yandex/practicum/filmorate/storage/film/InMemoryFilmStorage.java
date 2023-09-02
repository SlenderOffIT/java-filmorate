package ru.yandex.practicum.filmorate.storage.film;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.FilmSortingCriteria.FilmSortingCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Getter
    private final Map<Integer, Film> storageFilm = new HashMap<>();
    private int id = 1;

    public List<Film> getFilms() {
        return new ArrayList<>(storageFilm.values());
    }

    @Override
    public List<Film> getSortedFilmsOfDirector(int directorId, FilmSortingCriteria creteria) {
        return null;
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        return null;
    }

    @Override
    public List<Film> popularGenreYearSearch(int genreId, int year, int limit) {
        return null;
    }

    public Film findFilmById(int id) {
        return storageFilm.get(id);
    }

    public Film save(Film film) {
        film.setId(id++);
        storageFilm.put(film.getId(), film);
        log.debug("Добавили фильм с id {}", film.getId());
        return film;
    }

    public Film update(Film film) {
        storageFilm.put(film.getId(), film);
        log.debug("Изменили данные о фильме {}", film.getName());
        return film;
    }

    public void delete(int id) {
        storageFilm.remove(id);
        log.debug("Фильм с id {} удален", id);
    }

    @Override
    public void likeForFilm(int id, int userId) {
        Film film = getStorageFilm().get(id);
        if (!isExistLike(id, userId)) {
            film.getFilmLikes().add(userId);
            film.incrementRating();
            update(film);
            log.debug("Пользователь с id {} поставил лайк фильму с id {}", userId, id);
        }
    }

    @Override
    public void deleteLikeForFilm(int id, int userId) {
        Film film = getStorageFilm().get(id);
        if (isExistLike(id, userId)) {
            film.getFilmLikes().remove(userId);
            film.decrementRating();
            update(film);
            log.debug("Пользователь с id {} удалил лайк с фильма с id {}", userId, id);
        }
    }

    public boolean isExist(int id) {
        return getStorageFilm().containsKey(id);
    }

    @Override
    public boolean isExistLike(int filmId, int userId) {
        Film film = getStorageFilm().get(filmId);
        return film.getFilmLikes().contains(userId);
    }
}
