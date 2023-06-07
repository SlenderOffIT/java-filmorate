package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс FilmService выполняет:
 * likeForFilm - добавлением лайка для фильма;
 * deleteLikeForFilm - удалением лайка у фильма;
 * topFilms - выводит список топовых фильмов по лайкам.
 */
@Slf4j
@Service
public class FilmService {

    InMemoryFilmStorage inMemoryFilmStorage;
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Integer likeForFilm(int id, int userId) {
        if (!inMemoryUserStorage.getStorageUsers().containsKey(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован", id));
        }
        Film film = inMemoryFilmStorage.getStorageFilm().get(id);
        film.setFilmLikes(userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, id);
        return film.getFilmLikes().size();
    }

    public Integer deleteLikeForFilm(int id, int userId) {
        if (!inMemoryUserStorage.getStorageUsers().containsKey(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован", id));
        }
        Film film = inMemoryFilmStorage.getStorageFilm().get(id);
        film.getFilmLikes().remove(userId);
        log.info("Пользователь с id {} удалил лайк с фильма с id {}", userId, id);
        return film.getFilmLikes().size();
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
        List<Film> listFilms = new ArrayList<>(inMemoryFilmStorage.getStorageFilm().values());
        if (count == null || count <= 0) {
            listFilms.sort(Comparator.comparingInt((Film film) -> film.getFilmLikes().size()).reversed());
            log.info("Пользователь запросил топ 10 фильмов");
            return listFilms;
        } else {
            List<Film> popularFilms = listFilms.stream()
                    .sorted(Comparator.comparingInt((Film film) -> film.getFilmLikes().size()).reversed())
                    .limit(count)
                    .collect(Collectors.toList());
            log.info("Пользователь запросил топ {} фильмов", count);
            return popularFilms;
        }
    }
}
