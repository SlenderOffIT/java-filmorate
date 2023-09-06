package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewServiceImpl;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private ReviewServiceImpl reviewService;

    @PostMapping()
    public Review createReview(@Valid @RequestBody Review review) {
        log.debug("Поступил запрос на создание отзыва");
        return reviewService.createReview(review);
    }

    @GetMapping()
    public Collection<Review> getReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(required = false, defaultValue = "10") Integer count
    ) {
        log.debug(String.format("Поступил запрос на получение всех отзывов фильма с id %s", filmId));
        return reviewService.getAllReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        log.debug(String.format("Поступил запрос на получение отзыва с id %s", id));
        return reviewService.getReviewById(id);
    }

    @PutMapping()
    public Review updateReview(@RequestBody Review review) {
        log.debug(String.format("Поступил запрос на обновление отзыва с id %s", review.getReviewId()));
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable Integer id) {
        log.debug(String.format("Поступил запрос на удаление отзыва с id %s", id));
        reviewService.deleteReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug(String.format("Поступил запрос на добавление лайка отзыву с id %s от пользователя с id %s", id, userId));
        reviewService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug(String.format("Поступил запрос на удаление лайка у отзыва с id %s от пользователя с id %s", id, userId));
        reviewService.removeLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void setDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug(String.format("Поступил запрос на добавление дизлайка отзыву с id %s от пользователя с id %s", id, userId));
        reviewService.setDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug(String.format("Поступил запрос на удаление дизлайка у отзыва с id %s от пользователя с id %s", id, userId));
        reviewService.removeDislike(id, userId);
    }
}
