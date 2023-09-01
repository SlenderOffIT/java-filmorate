package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.AlreadyExists.DirectorAlreadyExisitsException;
import ru.yandex.practicum.filmorate.exception.NotFound.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@Validated
public class DirectorService {

    DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        log.debug("Обрабатываем запрос на просмотр всех режиссеров.");
        return directorStorage.getAllDirectors();
    }

    public Director getDirectorById(int id) {
        throwsIfDoesNotExist(id);
        log.debug("Обрабатываем запрос на просмотр режиссера с id {}.", id);
        return directorStorage.getDirectorById(id);
    }

    public Director saveDirector(@Valid Director director) {
        throwsIfExists(director.getId());
        log.debug("Обрабатываем запрос на создание режиссера.");
        return directorStorage.saveDirector(director);
    }

    public Director updateDirector(@Valid Director director) {
        throwsIfDoesNotExist(director.getId());
        log.debug("Обрабатываем запрос на изменение режиссера с id {}.", director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        throwsIfDoesNotExist(id);
        log.debug("Обрабатываем запрос на удаление режиссера с id {}.", id);
        directorStorage.deleteDirectorById(id);
    }

    private void throwsIfDoesNotExist(int id) throws DirectorNotFoundException {
        if (!directorStorage.isExist(id)) {
            log.warn("Режиссера с таким id не существует {}", id);
            throw new DirectorNotFoundException(String.format("Режиссер с id = %d не найден", id));
        }
    }

    private void throwsIfExists(int id) throws DirectorAlreadyExisitsException {
        if (directorStorage.isExist(id)) {
            log.warn("Режиссер с таким id уже существует {}", id);
            throw new DirectorAlreadyExisitsException(String.format("Режиссер с id = %d уже существует", id));
        }
    }
}

