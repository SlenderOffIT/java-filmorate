package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    List<Review> getAll();

    List<Review> getAllByCount(Integer count);

    List<Review> getAllById(Integer filmId, Integer count);

    Review getById(Integer id);

    Review create(Review review);

    Review update(Review review);

    void deleteById(Integer id);

    void addLike(Integer reviewId, Integer userId);

    void deleteLike(Integer reviewId, Integer userId);

    void addDislike(Integer reviewId, Integer userId);

    void deleteDislike(Integer reviewId, Integer userId);
}
