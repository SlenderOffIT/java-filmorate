package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Director getDirectorById(int id);

    Director saveDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirectorById(int id);

    boolean isExist(int id);
}