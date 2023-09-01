package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

/**
 * Контроллер режиссеров:
 * /directors - просмотр режиссеров, добавление и редактирование режиссера;
 * /directors/{id} - просмотр и удаление режиссера по id;
 */

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/directors")
public class DirectorController {

    DirectorService directorService;

    @GetMapping
    public List<Director> getAllDirectors() {
        log.debug("Поступил запрос на просмотр всех режиссеров.");
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр режиссера с id {}.", id);
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director saveDirector(@RequestBody Director director) {
        log.debug("Поступил запрос на сохранение режиссера.");
        return directorService.saveDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        log.debug("Поступил запрос на изменение режиссера с id {}.", director.getId());
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable int id) {
        log.debug("Поступил запрос на удаление режиссера с id {}.", id);
        directorService.deleteDirector(id);
    }
}