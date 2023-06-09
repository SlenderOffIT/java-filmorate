package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film findFilmById(int id);

    Film postFilm(Film film);

    Film update(Film film);

    void delete(int id);

    Map<Integer, Film> getStorageFilm();

    boolean isExist(int id);
}
