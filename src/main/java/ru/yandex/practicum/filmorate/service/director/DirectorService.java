package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    List<Director> getAllDirectors();

    Director getDirectorById(int id);

    Director saveDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);
}
