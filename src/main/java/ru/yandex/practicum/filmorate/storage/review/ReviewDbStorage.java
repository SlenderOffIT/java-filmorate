package ru.yandex.practicum.filmorate.storage.review;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFound.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;

@Repository
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Review> getAll() {
        List<Review> reviews;
        reviews = jdbcTemplate.query("SELECT * FROM reviews", reviewRowMapper());
        return reviews;
    }

    @Override
    public List<Review> getAllByCount(Integer count) {
        List<Review> reviews;
        reviews = jdbcTemplate.query("SELECT * FROM reviews order by useful desc limit ?", reviewRowMapper(), count);
        return reviews;
    }

    @Override
    public List<Review> getAllById(Integer filmId, Integer count) {
        List<Review> reviews;
        reviews = jdbcTemplate.query("SELECT * FROM reviews where film_id = ? order by useful desc limit ?", reviewRowMapper(), filmId, count);
        return reviews;
    }

    @Override
    public Review getById(Integer reviewId) {
        return jdbcTemplate.query("SELECT * FROM reviews where review_id = ?", reviewRowMapper(), reviewId).stream().findFirst()
                .orElseThrow(() -> new ReviewNotFoundException(String.format("Отзыва с id %s не существует", reviewId)));
    }

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        Map<String, Object> params = Map.of(
                "content", review.getContent(),
                "is_positive", review.getIsPositive(),
                "user_id", review.getUserId(),
                "film_id", review.getFilmId(),
                "useful", 0);
        Number number = simpleJdbcInsert.executeAndReturnKey(params);
        review.setReviewId(number.intValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        jdbcTemplate.update("UPDATE reviews SET content = ?, is_positive = ?, useful = ? WHERE review_id = ?",
                review.getContent(),
                review.getIsPositive(),
                getUseful(review.getReviewId()),
                review.getReviewId());
        return getById(review.getReviewId());
    }

    @Override
    public void deleteById(Integer reviewId) {
        Review review = getById(reviewId);
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", reviewId);
    }

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        Set<Integer> likesReviewById = new HashSet<>(
                jdbcTemplate.query("SELECT * FROM review_likes where REVIEW_ID = ? and IS_POSITIVE = true",
                        (rs, rowNum) -> rs.getInt("user_id"), reviewId));
        if (!likesReviewById.contains(userId)) {
            jdbcTemplate.update("INSERT INTO review_likes(review_id, user_id, is_positive) VALUES (?, ?, ?)",
                    reviewId, userId, true);
            jdbcTemplate.update("UPDATE reviews SET useful = ? where review_id = ?", getUseful(reviewId), reviewId);
        }
    }

    @Override
    public void deleteLike(Integer reviewId, Integer userId) {
        Set<Integer> likesReviewById = new HashSet<>(
                jdbcTemplate.query("SELECT * FROM review_likes where REVIEW_ID = ? and IS_POSITIVE = true",
                        (rs, rowNum) -> rs.getInt("user_id"), reviewId));
        if (likesReviewById.contains(userId)) {
            jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? and user_id = ? and is_positive = true",
                    reviewId, userId);
            jdbcTemplate.update("UPDATE reviews SET useful = ? where review_id = ?", getUseful(reviewId), reviewId);
        }
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        Set<Integer> likesReviewById = new HashSet<>(
                jdbcTemplate.query("SELECT * FROM review_likes where REVIEW_ID = ? and IS_POSITIVE = false",
                        (rs, rowNum) -> rs.getInt("user_id"), reviewId));
        if (!likesReviewById.contains(userId)) {
            jdbcTemplate.update("INSERT INTO review_likes(review_id, user_id, is_positive) VALUES (?, ?, ?)", reviewId, userId, false);
            jdbcTemplate.update("UPDATE reviews SET useful = ? where review_id = ?", getUseful(reviewId), reviewId);
        }
    }

    @Override
    public void deleteDislike(Integer reviewId, Integer userId) {
        Set<Integer> likesReviewById = new HashSet<>(
                jdbcTemplate.query("SELECT * FROM review_likes where REVIEW_ID = ? and IS_POSITIVE = false",
                        (rs, rowNum) -> rs.getInt("user_id"), reviewId));
        if (likesReviewById.contains(userId)) {
            jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? and user_id = ? and is_positive = false", reviewId, userId);
            jdbcTemplate.update("UPDATE reviews SET useful = ? where review_id = ?", getUseful(reviewId), reviewId);
        }
    }

    private RowMapper<Review> reviewRowMapper() {
        return (rs, rowNum) -> {
            Review review = new Review(
                    rs.getString("content"),
                    rs.getBoolean("is_positive"),
                    rs.getInt("user_id"),
                    rs.getInt("film_id")
            );
            review.setReviewId(rs.getInt("review_id"));
            review.setUseful(getUseful(review.getReviewId()));
            return review;
        };
    }

    private Integer getUseful(Integer reviewId) {
        Integer countLikes = jdbcTemplate.queryForObject("SELECT count(*) AS count " +
                        "FROM review_likes " +
                        "WHERE review_id = ? and is_positive = true",
                (rs, rowNum) -> rs.getInt("count"), reviewId);
        Integer countDislikes = jdbcTemplate.queryForObject("SELECT count(*) AS count " +
                        "FROM review_likes " +
                        "WHERE review_id = ? and is_positive = false",
                (rs, rowNum) -> rs.getInt("count"), reviewId);
        Integer count = 0;
        count += countLikes;
        count -= countDislikes;
        return count;
    }
}