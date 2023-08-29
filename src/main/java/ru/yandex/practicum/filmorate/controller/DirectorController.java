package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

/**
 * Контроллер жанров:
 * /directors - просмотр всех жанров;
 * /directors/{id} - просмотр жанра по id.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {

    DirectorService directorService;

    @GetMapping
    public List<Director> getAllDirectors() {
        log.debug("Поступил запрос на просмотр всех жанров.");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр жанра с id {}.", id);
        return directorService.getDirectorById(id);
    }
}