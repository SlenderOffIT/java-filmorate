package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {

    FilmController filmController;
    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;
    FilmService filmService;
    Film film;
    Film film1;
    Film film2;
    Film film3;
    Film film4;
    Film film5;
    Film film6;
    Film film7;
    Film film8;
    Film film9;
    Film film10;

    User user;
    User user1;
    User user2;
    User user3;


    @BeforeEach
    void create() {
        inMemoryFilmStorage = new InMemoryFilmStorage();
        inMemoryUserStorage = new InMemoryUserStorage();
        filmService = new FilmService(inMemoryFilmStorage, inMemoryUserStorage);
        filmController = new FilmController(filmService);

        film = new Film("Касандра", "описание",
                LocalDate.of(1992, 12, 1), 123);
        film1 = new Film("Боевик", "Описание",
                LocalDate.of(2019, 11, 2), 210);
        film2 = new Film("Комедия", "описание",
                LocalDate.of(2020, 10, 9), 222);
        film3 = new Film("Ужасы", "Описание",
                LocalDate.of(2018, 9, 12), 60);
        film4 = new Film("Триллер", "описание",
                LocalDate.of(1902, 8, 11), 90);
        film5 = new Film("Аниме", "Описание",
                LocalDate.of(2016, 7, 7), 310);
        film6 = new Film("Вестерн", "описание",
                LocalDate.of(2015, 6, 8), 123);
        film7 = new Film("Уже не знаю что", "Описание",
                LocalDate.of(1932, 5, 9), 190);
        film8 = new Film("Какой-то фильм", "описание",
                LocalDate.of(2013, 4, 29), 203);
        film9 = new Film("Выживший", "Описание",
                LocalDate.of(1966, 3, 12), 120);
        film10 = new Film("Во все тяжкие", "описание",
                LocalDate.of(2011, 1, 11), 53);

        user = new User("asdfg@gmail.com", "Baobab", "Вася", LocalDate.of(1995, 12, 28));
        user1 = new User("qwert@mail.ru", "Grinch", LocalDate.of(2000, 12, 22));
        user2 = new User("zxcvb@gmail.com", "Leo", "Alex", LocalDate.of(1954, 5, 12));
        user3 = new User("lkjhg@mail.ru", "Dump", LocalDate.of(1993, 1, 19));
    }

    @Test
    void likeForFilm() {
        Film filmOrigin = inMemoryFilmStorage.postFilm(film);
        inMemoryUserStorage.postUser(user);
        inMemoryUserStorage.postUser(user1);

        filmService.likeForFilm(1, 1);
        filmService.likeForFilm(1, 2);

        assertEquals(2, filmOrigin.getFilmLikes().size(), "Количество лайков не совпадает");
    }

    @Test
    void deleteLikeForFilm() {
        Film filmOrigin = inMemoryFilmStorage.postFilm(film);
        inMemoryUserStorage.postUser(user);
        inMemoryUserStorage.postUser(user1);

        filmService.likeForFilm(1, 1);
        filmService.likeForFilm(1, 2);

        assertEquals(2, filmOrigin.getFilmLikes().size(), "Количество лайков не совпадает");

        filmService.deleteLikeForFilm(1, 1);
        assertEquals(1, filmOrigin.getFilmLikes().size(), "Количество лайков не совпадает");
    }

    @Test
    void topFilms() {
        Film filmOrigin = inMemoryFilmStorage.postFilm(film);
        Film filmOrigin1 = inMemoryFilmStorage.postFilm(film1);
        Film filmOrigin2 = inMemoryFilmStorage.postFilm(film2);
        Film filmOrigin3 = inMemoryFilmStorage.postFilm(film3);
        Film filmOrigin4 = inMemoryFilmStorage.postFilm(film4);
        Film filmOrigin5 = inMemoryFilmStorage.postFilm(film5);
        Film filmOrigin6 = inMemoryFilmStorage.postFilm(film6);
        Film filmOrigin7 = inMemoryFilmStorage.postFilm(film7);
        Film filmOrigin8 = inMemoryFilmStorage.postFilm(film8);
        Film filmOrigin9 = inMemoryFilmStorage.postFilm(film9);
        Film filmOrigin10 = inMemoryFilmStorage.postFilm(film10);

        inMemoryUserStorage.postUser(user);
        inMemoryUserStorage.postUser(user1);
        inMemoryUserStorage.postUser(user2);
        inMemoryUserStorage.postUser(user3);

        filmService.likeForFilm(10, 1);
        filmService.likeForFilm(10, 2);
        filmService.likeForFilm(10, 3);
        filmService.likeForFilm(10, 4);
        filmService.likeForFilm(5, 1);
        filmService.likeForFilm(5, 2);
        filmService.likeForFilm(5, 3);
        filmService.likeForFilm(1, 2);
        filmService.likeForFilm(1, 4);
        filmService.likeForFilm(7, 2);

        assertEquals(4, filmOrigin9.getFilmLikes().size(), "Количество лайков не совпадает");
        assertEquals(3, filmOrigin4.getFilmLikes().size(), "Количество лайков не совпадает");
        assertEquals(2, filmOrigin.getFilmLikes().size(), "Количество лайков не совпадает");
        assertEquals(1, filmOrigin6.getFilmLikes().size(), "Количество лайков не совпадает");

        List<Film> top5Films = filmService.topFilms(4);

        assertEquals(4, top5Films.size());
        assertEquals(10, top5Films.get(0).getId(), "Id не совпадают.");
        assertEquals(5, top5Films.get(1).getId(), "Id не совпадают.");
        assertEquals(1, top5Films.get(2).getId(), "Id не совпадают.");
        assertEquals(7, top5Films.get(3).getId(), "Id не совпадают.");

        filmService.deleteLikeForFilm(10, 1);
        filmService.deleteLikeForFilm(10, 2);
        filmService.deleteLikeForFilm(10, 3);
        filmService.deleteLikeForFilm(10, 4);

        top5Films = filmService.topFilms(3);

        assertEquals(5, top5Films.get(0).getId(), "Id не совпадают.");
        assertEquals(1, top5Films.get(1).getId(), "Id не совпадают.");
        assertEquals(7, top5Films.get(2).getId(), "Id не совпадают.");
    }
}