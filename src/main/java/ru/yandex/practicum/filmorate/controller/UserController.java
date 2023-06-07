package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

/**
 * Данный контроллер содержит в себе эндпоинты взаимодействия с пользователями:
 * /users - для добавления, изменения пользователя и просмотра всех пользователей;
 * /users/id - для просмотра пользователя по его id;
 * /users/id/friends - для просмотра списка друзей;
 * /users/id/friends/common/otherId - для просмотра общих друзей с другим пользователем;
 * /users/id/friends/friendId - для добавления и удаления друзей.
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;

    public UserController(InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return inMemoryUserStorage.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserId(@PathVariable int id) {
        return inMemoryUserStorage.getUserId(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> listFriends(@PathVariable int id) {
        return userService.listFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> listMutualFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.listMutualFriends(id, otherId);
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        return inMemoryUserStorage.postUser(user);
    }

    @PutMapping
    public User changeUser(@RequestBody User user) {
        return inMemoryUserStorage.changeUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addingFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.addingFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriends(id, friendId);
    }
}
