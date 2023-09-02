package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.FilmSortingCriteria.FilmSortingCriteria;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    List<Film> getFilms();

    List<Film> getSortedFilmsOfDirector(int directorId,
                                        FilmSortingCriteria creteria);

    List<Film> searchFilms(String query, String by);

    List<Film> popularGenreYearSearch(int genreId, int year, int limit);

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
