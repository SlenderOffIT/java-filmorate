package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.feed.InMemoryFeedStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmControllerTest {

    FilmController filmController;
    InMemoryUserStorage inMemoryUserStorage;
    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryFeedStorage inMemoryFeedStorage;
    FilmServiceImpl filmService;
    Film film;
    Film film1;

    @BeforeEach
    void create() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        inMemoryFeedStorage = new InMemoryFeedStorage();  // Инициализируем хранилище
        filmService = new FilmServiceImpl(inMemoryFilmStorage, inMemoryUserStorage, inMemoryFeedStorage);  // Передаем в конструктор
        filmController = new FilmController(filmService);

        film = new Film("Касандра", "описание",
                LocalDate.of(2021, 4, 21), 123);
        film1 = new Film("Боевик", "Описание",
                LocalDate.of(2021, 1, 12), 210);
    }

    @Test
    void getFilms() {
        filmController.postFilm(film);
        filmController.postFilm(film1);

        Collection<Film> films = filmController.getFilms();
        assertEquals(2, films.size());
    }

    @Test
    void postFilm() {
        filmController.postFilm(film);
        filmController.postFilm(film1);

        assertEquals(film, inMemoryFilmStorage.getStorageFilm().get(1));
        assertEquals(film1, inMemoryFilmStorage.getStorageFilm().get(2));
    }

    @Test
    void putFilm() {
        filmController.postFilm(film);
        filmController.postFilm(film1);

        Film film2 = film1;
        film2.setDescription("Поменяем описание");
        filmController.putFilm(film2);

        assertEquals(2, film2.getId());
        assertEquals(2, inMemoryFilmStorage.getStorageFilm().size());
    }
}