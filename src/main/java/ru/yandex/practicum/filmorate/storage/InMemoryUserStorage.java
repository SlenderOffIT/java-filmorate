package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.filmorate.util.Validations.validation;

/**
 * Данный класс InMemoryUserStorage предназначен для хранения, обновления и поиска пользователей нашего сервиса.
 * getUsers - возвращает список всех пользователей.
 * getUserId - возвращает пользователя по id.
 * postUser - метод для создания пользователя в котором при создании происходит валидация в классе Validation
 * на правильность создания пользователя.
 * changeUser - метод для изменения данных о пользователе.
 */
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private int idUsers = 1;
    @Getter
    private final Map<Integer, User> storageUsers = new HashMap<>();

    public List<User> getUsers() {
        log.info("Поступил запрос на просмотр списка всех пользователей");
        return new ArrayList<>(storageUsers.values());
    }

    public User getUserId(int id) {
        if (!storageUsers.containsKey(id) || id <= 0) {
            throw new UserNotFoundException(String.format("Не корректный id %d пользователя", id));
        }
        return storageUsers.get(id);
    }

    public User postUser(User user) {
        for (User userEmail: storageUsers.values()) {
            if (userEmail.getEmail().equals(user.getEmail())) {
                log.debug("Попытка создать пользователя по уже существующему email {}", user.getEmail());
                throw new ValidationException(String.format("Пользователь с таким email %s уже существует.", user.getEmail()));
            }
        }
        validation(user);
        user.setId(idUsers++);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        storageUsers.put(user.getId(), user);
        log.info("Пользователь {} добавлен.", user.getLogin());
        return user;
    }

    public User changeUser(User user) {
        if (storageUsers.containsKey(user.getId())) {
            validation(user);
            storageUsers.put(user.getId(), user);
            log.info("Пользователь {} изменен", user.getLogin());
        } else {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", user.getId()));
        }
        return user;
    }
}
