package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    List<Film> getFilms();

    List<Film> getSortedFilmsOfDirector(int directorId,
                                               FilmDbStorage.SortingCreteria creteria);

    Film findFilmById(int id);

    Film save(Film film);

    Film update(Film film);

    void delete(int id);

    Map<Integer, Film> getStorageFilm();

    void likeForFilm(int id, int userId);

    void deleteLikeForFilm(int id, int userId);

    boolean isExist(int id);

    boolean isExistLike(int filmId, int userId);

}
