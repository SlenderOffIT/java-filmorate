package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        return directorStorage.getDirectorById(id);// проверка isExist переехала на уровень Storage,
        //исключение выбрасывается сразу после проверки
    }

    public Director saveDirector(Director director) {
        log.debug("Обрабатываем запрос на создание режиссера.");
        return directorStorage.saveDirector(director);
    }

    public Director updateDirector(Director director) {
        log.debug("Обрабатываем запрос на изменение режиссера с id {}.", director.getId());
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        log.debug("Обрабатываем запрос на удаление режиссера с id {}.", id);
        directorStorage.deleteDirectorById(id);
    }
}

