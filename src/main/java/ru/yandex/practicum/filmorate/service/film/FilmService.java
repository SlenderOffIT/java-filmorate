package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.FilmSortingCriteria.FilmSortingCriteria;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Collection<Film> getFilms();
    Film findFilmById(int id);
    Film postFilm(Film film);
    Film update(Film film);
    void delete(int id);
    void likeForFilm(int id, int userId);
    void deleteLikeForFilm(int id, int userId);
    List<Film> topFilms(Integer count, Integer genreId, Integer year);
    List<Film> getSortedFilmsOfDirector(int directorId,
                                        FilmSortingCriteria criteria);
    List<Film> searchFilms(String query, String by);
    List<Film> getCommonFilms(Integer userId, Integer friendId);
}
