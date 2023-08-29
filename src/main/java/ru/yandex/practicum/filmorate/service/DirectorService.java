package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService {

    DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        log.debug("Обрабатываем запрос на просмотр всех режиссеров.");
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        log.debug("Обрабатываем запрос на просмотр режиссера с id {}.", id);
        if (directorStorage.isExist(id)) {
            return directorStorage.getDirectorById(id);
        } else {
            log.debug("Режиссера с таким id не существующего {}", id);
            throw new DirectorNotFoundException(String.format("Режиссера с таким id %d не существует.", id));
        }
    }


    //TODO заглушка createDirector
    public Director createDirector(Director director) {
        log.debug("Обрабатываем запрос на создание режиссера.");
        if (0 == director.getId() || !directorStorage.isExist(director.getId())) {
            return directorStorage.getDirectorById(director.getId());
        } else {
            log.debug("Режиссера с id = {} не существует.", director.getId());
            throw new DirectorNotFoundException(String.format("Режиссера с таким id %d не существует.", director.getId()));
        }
    }

    //TODO заглушка updateDirector
    public Director updateDirector(Director director) {
        log.debug("Обрабатываем запрос на изменение режиссера с id {}.", director.getId());
        if (directorStorage.isExist(director.getId())) {
            return directorStorage.getDirectorById(director.getId());
        } else {
            log.debug("Режиссера с id = {} не существует.", director.getId());
            throw new DirectorNotFoundException(String.format("Режиссера с таким id %d не существует.", director.getId()));
        }
    }
}
