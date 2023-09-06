package ru.yandex.practicum.filmorate.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFound.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFound.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFound.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
public class Validations {

    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private ReviewStorage reviewStorage;

    public Validations(UserStorage userStorage, FilmStorage filmStorage, ReviewStorage reviewStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.reviewStorage = reviewStorage;
    }

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

    public static void validation(@RequestBody Review review,
                                  UserStorage userStorage,
                                  FilmStorage filmStorage,
                                  ReviewStorage reviewStorage) {

        if (review.getUserId() <= 0) {
            log.debug("При создании отзыва был передан отрицательный id пользователя {}.", review.getUserId());
            throw new UserNotFoundException(String.format("Id пользователя не может быть отрицательным %s.", review.getUserId()));
        }
        if (!userStorage.isExist(review.getUserId())) {
            log.debug("При создании отзыва был передан не существующий id пользователя {}.", review.getUserId());
            throw new IncorrectParameterException(String.format("Пользователя с id %s не существует", review.getUserId()));
        }
        if (review.getFilmId() <= 0) {
            log.debug("При создании отзыва был передан отрицательный id фильма {}.", review.getFilmId());
            throw new FilmNotFoundException(String.format("Id фильма не может быть отрицательным %s.", review.getFilmId()));
        }
        if (!filmStorage.isExist(review.getFilmId())) {
            log.debug("При создании отзыва был передан не существующий id фильма {}.", review.getFilmId());
            throw new IncorrectParameterException(String.format("Фильма с id %s не существует", review.getFilmId()));
        }
        Optional<Review> first = reviewStorage.getAll().stream()
                .filter(review1 -> (review1.getFilmId().equals(review.getFilmId())
                        && review1.getUserId().equals(review.getUserId()))).findFirst();
        if (first.isPresent()) {
            log.debug("Пользователь с id {}, пытается написать два отзыва к одному фильму с id {}.", review.getUserId(), review.getFilmId());
            throw new IncorrectParameterException(String.format("Отзыв от пользователя с id %s для фильма с id %s уже существует.", review.getUserId(), review.getFilmId()));
        }
    }

    public static void validateReview(Integer id, ReviewStorage reviewStorage) {
        if (reviewStorage.getById(id) == null) {
            log.debug("Отзыва с id {} не существует", id);
            throw new ReviewNotFoundException(String.format("Отзыва с id %s не существует", id));
        }
    }

    public static void validateUser(Integer userId, UserStorage userStorage) {
        if (!userStorage.isExist(userId)) {
            log.debug("Пользователя с id {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
    }
}
