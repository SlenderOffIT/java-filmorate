package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

/**
 * Данный контроллер содержит в себе эндпоинты взаимодействия с пользователями:
 * /users - для добавления, изменения пользователя и просмотра всех пользователей;
 * /users/id - для просмотра пользователя по его id;
 * /users/id/friends - для просмотра списка друзей;
 * /users/id/friends/common/otherId - для просмотра общих друзей с другим пользователем;
 * /users/id/friends/friendId - для добавления и удаления друзей.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public User getUserId(@PathVariable int id) {
        return userService.getUserId(id);
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
        return userService.postUser(user);
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addingFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.addingFriends(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriends(id, friendId);
    }
}