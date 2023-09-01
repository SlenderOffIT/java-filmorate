package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

/**
 * Контроллер взаимодействия с пользователями:
 * /users - добавление, изменение пользователя и просмотр всех пользователей;
 * /users/{id} - просмотр пользователя по id;
 * /users/{id}/friends - просмотр списка друзей;
 * /users/{id}/friends/common/{otherId} - просмотр общих друзей с другим пользователем;
 * /users/{id}/friends/{friendId} - добавление и удаление друзей.
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @GetMapping
    public List<User> getUsers() {
        log.debug("Поступил запрос на просмотр всех пользователей.");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserId(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр пользователя с id {}.", id);
        return userService.getUserId(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> listFriends(@PathVariable int id) {
        log.debug("Поступил запрос на просмотр списка друзей пользователя с id {}.", id);
        return userService.listFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> listMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        log.debug("Поступил запрос на просмотр списка общих друзей у пользователя с id {} с пользователем с id {}.", id, otherId);
        return userService.listMutualFriends(id, otherId);
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        log.debug("Поступил запрос на создание пользователя с id {}.", user.getId());
        return userService.postUser(user);
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        log.debug("Поступил запрос на изменение пользователя с id {}.", user.getId());
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addingFriends(@PathVariable int id, @PathVariable int friendId) {
        log.debug("Поступил запрос на добавление в друзья от пользователя с id {}, пользователю с id {}.", id, friendId);
        userService.addingFriends(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.debug("Поступил запрос на удаление пользователя с id {}.", id);
        userService.deleteUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable int id, @PathVariable int friendId) {
        log.debug("Поступил запрос на удаление из друзей от пользователя с id {}, пользователя с id {}.", id, friendId);
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeed(@PathVariable int id) {
        List<Feed> feeds = userService.getFeed(id);
        if (feeds.size() > 9) {
            feeds = feeds.subList(0, 9);
        }
        return feeds;
    }

}