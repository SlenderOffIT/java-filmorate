package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class ValidationUser {

    public static void validation(@RequestBody User user) {
        LocalDate today = LocalDate.now();
        LocalDate birthday = user.getBirthday();

        if (!user.getEmail().contains("@")) {
            log.trace("Пользователь ввел не правильный email " + user.getEmail());
            throw new ValidationException("Некорректный email адрес.");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.trace("пользователь ввел не правильный логин " + user.getLogin());
            throw new ValidationException("Логин должен быть без пробелов и не должен быть пустым.");
        } else if (birthday.isAfter(today)) {
            log.trace("пользователь ввел не правильную дату рождения " + user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
