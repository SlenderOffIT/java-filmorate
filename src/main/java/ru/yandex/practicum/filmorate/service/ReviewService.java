package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

/**
 * Класс ReviewService выполняет:
 * createReview - создание отзыва;
 * getAllReviews - метод возвращает все отзывы на определенный фильм, либо все отзывы;
 * getFilmById - возвращает все отзывы на фильм;
 * updateReview - обновление отзыва;
 * deleteReviewById - удаление отзыва по id;
 * setLike - добавление лайка к отзыву;
 * removeLike - удаление лайка;
 * setDislike - добавление дизлайка;
 * removeDislike - удаление дизлайка.
 */
@Service
@Slf4j
public class ReviewService {

    private ReviewStorage reviewStorage;
    private UserStorage userStorage;
    private FilmStorage filmStorage;

    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Review createReview(Review review) {
        log.debug("Обрабатываем запрос на создание отзыва к фильму c id {}", review.getFilmId());
        validateCreateReview(review);
        return reviewStorage.create(review);
    }

    public List<Review> getAllReviews(Integer filmId, Integer count) {
        List<Review> allReviews;
        if (filmId == null) {
            log.debug("Обрабатываем запрос на просмотр отзывов в количестве {} штук.", count);
            allReviews = reviewStorage.getAllByCount(count);
            return allReviews;
        }
        log.debug("Обрабатываем запрос на просмотр всех отзывов фильма с id {}.", filmId);
        allReviews = reviewStorage.getAllById(filmId, count);
        return allReviews;
    }

    public Review getReviewById(Integer id) {
        validateReview(id);
        log.debug("Обрабатываем запрос на получение отзыва с id {}.", id);
        Review reviewById = reviewStorage.getById(id);
        return reviewById;
    }

    public Review updateReview(Review review) {
        validateReview(review.getReviewId());
        log.debug("Обрабатываем запрос на обновление отзыва с id {}.", review.getReviewId());
        return reviewStorage.update(review);
    }

    public void deleteReviewById(Integer id) {
        log.debug("Обрабатываем запрос на удаление отзыва с id {}.", id);
        validateReview(id);
        reviewStorage.deleteById(id);
    }

    public void setLike(Integer reviewId, Integer userId) {
        log.debug("Обрабатываем запрос от пользователя id {} поставить лайк отзыву id {}", userId, reviewId);
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void removeLike(Integer reviewId, Integer userId) {
        log.debug("Обрабатываем запрос от пользователя id {} удалить лайк у отзыва id {}", userId, reviewId);
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void setDislike(Integer reviewId, Integer userId) {
        log.debug("Обрабатываем запрос от пользователя id {} поставить дизлайк отзыву id {}", userId, reviewId);
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeDislike(Integer reviewId, Integer userId) {
        log.debug("Обрабатываем запрос от пользователя id {} удалить дизлайк у отзыва id {}", userId, reviewId);
        validateReview(reviewId);
        validateUser(userId);
        reviewStorage.deleteDislike(reviewId, userId);
    }

    private void validateCreateReview(Review review) {
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

    private void validateReview(Integer id) {
        if (reviewStorage.getById(id) == null) {
            log.debug("Отзыва с id {} не существует", id);
            throw new ReviewNotFoundException(String.format("Отзыва с id %s не существует", id));
        }
    }

    private void validateUser(Integer userId) {
        if (!userStorage.isExist(userId)) {
            log.debug("Пользователя с id {} не существует", userId);
            throw new UserNotFoundException(String.format("Пользователя с id %s не существует", userId));
        }
    }
}