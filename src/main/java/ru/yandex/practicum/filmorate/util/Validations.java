package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class Validations {
    public static void validation(@RequestBody Film film) {
        final LocalDate realiseFilm = LocalDate.from(film.getReleaseDate());
        final LocalDate birthFilm = LocalDate.of(1895, 12, 28);

        if (film.getName().isEmpty()) {
            log.debug("Не добавили название фильма. Фильм от " + film.getReleaseDate() + " года.");
            throw new ValidationException("У фильма должно быть название.");
        } else if (film.getDescription().length() > 200) {
            log.debug("Переборщили с описанием " + film.getDescription());
            throw new ValidationException("Описание не должно превышать 200 символов");
        } else if (film.getDuration() <= 0) {
            log.debug("Не корректная продолжительность ввели " + film.getDuration());
            throw new ValidationException("Продолжительность не может быть отрицательной или равной нулю!");
        } else if (realiseFilm.isBefore(birthFilm)) {
            log.debug("Пытались добавить релиз которого был " + film.getReleaseDate());
            throw new ValidationException("Релиз фильма должен быть не раньше 28 декабря 1895 года.");
        }
    }

    public static void validation(@RequestBody User user) {
        LocalDate today = LocalDate.now();
        LocalDate birthday = user.getBirthday();

        if (!user.getEmail().contains("@")) {
            log.debug("Пользователь ввел не правильный email " + user.getEmail());
            throw new ValidationException("Некорректный email адрес.");
        } else if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.debug("пользователь ввел не правильный логин " + user.getLogin());
            throw new ValidationException("Логин должен быть без пробелов и не должен быть пустым.");
        } else if (birthday.isAfter(today)) {
            log.debug("пользователь ввел не правильную дату рождения " + user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
