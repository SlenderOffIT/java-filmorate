package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public class ValidationFilm {
    public static void validation(@RequestBody Film film) {
        final LocalDate realiseFilm = LocalDate.from(film.getReleaseDate());
        final LocalDate birthFilm = LocalDate.of(1895, 12, 28);

        if (film.getName().isEmpty()) {
            log.trace("Не добавили название фильма. Фильм от " + film.getReleaseDate() + " года.");
            throw new ValidationException("У фильма должно быть название.");
        } else if (film.getDescription().length() > 200) {
            log.trace("Переборщили с описанием " + film.getDescription());
            throw new ValidationException("Описание не должно превышать 200 символов");
        } else if (film.getDuration() <= 0) {
            log.trace("Не корректная продолжительность ввели " + film.getDuration());
            throw new ValidationException("Продолжительность не может быть отрицательной или равной нулю!");
        } else if (realiseFilm.isBefore(birthFilm)) {
            log.trace("Пытались добавить релиз которого был " + film.getReleaseDate());
            throw new ValidationException("Релиз фильма должен быть не раньше 28 декабря 1895 года.");
        }
    }
}
