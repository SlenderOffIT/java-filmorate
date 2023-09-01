package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFound.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GenreService {

    GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        log.debug("Обрабатываем запрос на просмотр всех жанров фильмов.");
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        log.debug("Обрабатываем запрос на просмотр фильма с id {}.", id);
        if (genreStorage.isExist(id)) {
            return genreStorage.getGenreById(id);
        } else {
            log.debug("Жанра с таким id не существующего {}", id);
            throw new GenreNotFoundException(String.format("Жанра с таким id %d не существует.", id));
        }
    }
}
