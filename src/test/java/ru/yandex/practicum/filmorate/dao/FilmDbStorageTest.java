package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private Film film;
    private User user;

    @BeforeEach
    public void create () {
        user = new User("asdfg@gmail.com", "Baobab", "Вася", LocalDate.of(1995, 12, 28));
        film = new Film("Касандра", "описание",
                LocalDate.of(1992, 12, 1), 123, new RatingMpa(1));
    }

    @Test
    public void testGetFilm() {
        filmDbStorage.postFilm(film);
        assertEquals(1, filmDbStorage.getFilms().size());
    }

    @Test
    public void testFindFilmById() {
        Film getFilm = filmDbStorage.postFilm(film);
        assertEquals(getFilm, filmDbStorage.findFilmById(1));
    }

    @Test
    public void testPostFilm() {
        filmDbStorage.postFilm(film);
        assertEquals(film, filmDbStorage.findFilmById(1));
    }

    @Test
    public void testUpdate() {
        Film getFilm = filmDbStorage.postFilm(film);
        getFilm.setName("Kokoshka");
        getFilm.setMpa(new RatingMpa(4));
        filmDbStorage.update(getFilm);
        assertEquals("Kokoshka", filmDbStorage.findFilmById(1).getName());
        assertEquals(getFilm, filmDbStorage.findFilmById(1));
    }

    @Test
    public void testDelete() {
        Film getFilm = filmDbStorage.postFilm(film);

        filmDbStorage.delete(getFilm.getId());
        assertTrue(filmDbStorage.getFilms().isEmpty());
    }

    @Test
    public void testLikeForFilm() {
        Film getFilm = filmDbStorage.postFilm(film);
        User likeUser = userDbStorage.postUser(user);

        filmDbStorage.likeForFilm(getFilm.getId(), likeUser.getId());
        assertEquals(1, filmDbStorage.findFilmById(1).getRate());
    }

    @Test
    public void testDeleteLikeForFilm() {
        Film getFilm = filmDbStorage.postFilm(film);
        User likeUser = userDbStorage.postUser(user);

        filmDbStorage.likeForFilm(getFilm.getId(), likeUser.getId());
        assertEquals(1, filmDbStorage.findFilmById(1).getRate());

        filmDbStorage.deleteLikeForFilm(getFilm.getId(), likeUser.getId());
        assertEquals(0, filmDbStorage.findFilmById(1).getRate());
    }
}
