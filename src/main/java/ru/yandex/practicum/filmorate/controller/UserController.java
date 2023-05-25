package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

import static ru.yandex.practicum.filmorate.util.ValidationUser.validation;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int idUsers = 1;
    @Getter
    private final Map<Integer, User> storageUsers = new HashMap<>();

    @GetMapping
    public List<User> getUsers() { // смотрим список всех пользователей
        log.info("Вывели список пользователей");
        return new ArrayList<>(storageUsers.values());
    }

    @PostMapping
    public User postUser(@RequestBody User user) { // создаем пользователя
        for (User userEmail: storageUsers.values()) {
            if (userEmail.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Пользователь с таким email " + user.getEmail() + "уже существует.");
            }
        }
        validation(user);
        user.setId(idUsers++);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        storageUsers.put(user.getId(), user);
        log.info("Пользователь " + user.getLogin() + " добавлен.");
        return user;
    }

    @PutMapping
    public User changeUser(@RequestBody User user) { // изменяем пользователя
        if (storageUsers.containsKey(user.getId())) {
            validation(user);
            storageUsers.put(user.getId(), user);
            log.info("Пользователь " + user.getLogin() + " изменен");
        } else {
            throw new ValidationException("Пользователя с таким id " + user.getId() + " не существует");
        }
        return user;
    }
}
