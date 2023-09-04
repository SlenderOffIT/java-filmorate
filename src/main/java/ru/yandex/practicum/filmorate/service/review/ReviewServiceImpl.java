package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.util.FeedType.*;
import static ru.yandex.practicum.filmorate.util.Validations.*;

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
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedStorage feedStorage;


    public ReviewServiceImpl(ReviewStorage reviewStorage,
                             @Qualifier("userDbStorage") UserStorage userStorage,
                             @Qualifier("filmDbStorage") FilmStorage filmStorage,
                             @Qualifier("feedDbStorage") FeedStorage feedStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.feedStorage = feedStorage;
    }

    @Override
    public Review createReview(Review review) {
        log.debug("Обрабатываем запрос на создание отзыва к фильму c id {}", review.getFilmId());
        validation(review, userStorage, filmStorage, reviewStorage);
        Review createdReview = reviewStorage.create(review);
        feedStorage.addFeed(createdReview.getUserId(), REVIEW.getValue(), ADD.getValue(), createdReview.getReviewId());
        return createdReview;
    }

    @Override
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

    @Override
    public Review getReviewById(Integer id) {
        validateReview(id, reviewStorage);
        log.debug("Обрабатываем запрос на получение отзыва с id {}.", id);
        return reviewStorage.getById(id);
    }

    @Override
    public Review updateReview(Review review) {
        validateReview(review.getReviewId(), reviewStorage);
        Review existingReview = getReviewById(review.getReviewId());
        feedStorage.addFeed(existingReview.getUserId(), REVIEW.getValue(), UPDATE.getValue(), existingReview.getReviewId());
        return reviewStorage.update(review);
    }

    @Override
    public void deleteReviewById(Integer id) {
        log.debug("Обрабатываем запрос на удаление отзыва с id {}.", id);
        validateReview(id, reviewStorage);
        Review review = getReviewById(id);
        feedStorage.addFeed(review.getUserId(), REVIEW.getValue(), REMOVE.getValue(), id);
        reviewStorage.deleteById(id);
    }

    @Override
    public void setLike(Integer reviewId, Integer userId) {
        log.debug("Обрабатываем запрос от пользователя c id {} поставить лайк отзыву id {}", userId, reviewId);
        validateReview(reviewId, reviewStorage);
        validateUser(userId, userStorage);
        reviewStorage.addLike(reviewId, userId);
    }

    @Override
    public void removeLike(Integer reviewId, Integer userId) {
        log.debug("Обрабатываем запрос от пользователя c id {} удалить лайк у отзыва id {}", userId, reviewId);
        validateReview(reviewId, reviewStorage);
        validateUser(userId, userStorage);
        reviewStorage.deleteLike(reviewId, userId);
    }

    @Override
    public void setDislike(Integer reviewId, Integer userId) {
        log.debug("Обрабатываем запрос от пользователя c id {} поставить дизлайк отзыву id {}", userId, reviewId);
        validateReview(reviewId, reviewStorage);
        validateUser(userId, userStorage);
        reviewStorage.addDislike(reviewId, userId);
    }

    @Override
    public void removeDislike(Integer reviewId, Integer userId) {
        log.debug("Обрабатываем запрос от пользователя c id {} удалить дизлайк у отзыва id {}", userId, reviewId);
        validateReview(reviewId, reviewStorage);
        validateUser(userId, userStorage);
        reviewStorage.deleteDislike(reviewId, userId);
    }
}