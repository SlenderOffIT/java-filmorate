package ru.yandex.practicum.filmorate.service.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review createReview(Review review);
    List<Review> getAllReviews(Integer filmId, Integer count);
    Review getReviewById(Integer id);
    Review updateReview(Review review);
    void deleteReviewById(Integer id);
    void setLike(Integer reviewId, Integer userId);
    void removeLike(Integer reviewId, Integer userId);
    void setDislike(Integer reviewId, Integer userId);
    void removeDislike(Integer reviewId, Integer userId);

}
