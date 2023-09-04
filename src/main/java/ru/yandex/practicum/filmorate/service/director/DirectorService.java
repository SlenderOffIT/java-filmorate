package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.model.Director;

import javax.validation.Valid;
import java.util.List;

public interface DirectorService {

    List<Director> getAllDirectors();
    Director getDirectorById(int id);
    Director saveDirector(@Valid Director director);
    Director updateDirector(@Valid Director director);
    void deleteDirector(int id);
}
