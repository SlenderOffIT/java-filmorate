package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewDbStorageTest {

    private final ReviewDbStorage reviewDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private Film film;
    private User user;
    private User user1;
    private Review review;
    private Review review1;


    @BeforeEach
    public void createObject() {
        user = new User("asdfg@gmail.com", "Baobab", "Вася", LocalDate.of(1995, 12, 28));
        user1 = new User("qwert@mail.ru", "Grinch", LocalDate.of(2000, 12, 22));
        film = new Film("Касандра", "описание", LocalDate.of(1992, 12, 1), 123, new RatingMpa(1));
        review = new Review("Отзыв1", true, 1,1);
        review1 = new Review("Отзыв2", false, 2,1);
    }

    @Test
    void getAll() {
        userDbStorage.save(user);
        userDbStorage.save(user1);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        reviewDbStorage.create(review1);
        List<Review> reviewList = reviewDbStorage.getAll();
        assertEquals(2, reviewList.size());
    }

    @Test
    void getAllByCount() {
        userDbStorage.save(user);
        userDbStorage.save(user1);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        reviewDbStorage.create(review1);
        List<Review> reviewList = reviewDbStorage.getAllByCount(1);
        assertEquals(1, reviewList.size());
        assertEquals(review, reviewList.get(0));
    }

    @Test
    void getAllById() {
        userDbStorage.save(user);
        userDbStorage.save(user1);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        reviewDbStorage.create(review1);
        List<Review> reviewList = reviewDbStorage.getAllById(film.getId(), 5);
        assertEquals(2, reviewList.size());
        assertEquals(review, reviewList.get(0));
        assertEquals(review1, reviewList.get(1));
    }

    @Test
    void getById() {
        userDbStorage.save(user);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        Review reviewTest = reviewDbStorage.getById(review.getReviewId());
        assertEquals(review, reviewTest);
    }

    @Test
    void create() {
        userDbStorage.save(user);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        assertEquals(review, reviewDbStorage.getById(review.getReviewId()));
    }

    @Test
    void update() {
        userDbStorage.save(user);
        filmDbStorage.save(film);
        Review reviewTest =  reviewDbStorage.create(review);
        reviewTest.setContent("Поменяли текст");
        reviewDbStorage.update(reviewTest);
        assertEquals("Поменяли текст", reviewDbStorage.getById(reviewTest.getReviewId()).getContent());
    }

    @Test
    void deleteById() {
        userDbStorage.save(user);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        assertEquals(1, reviewDbStorage.getAll().size());
        reviewDbStorage.deleteById(review.getReviewId());
        assertTrue(reviewDbStorage.getAll().isEmpty());
    }

    @Test
    void addLike() {
        userDbStorage.save(user);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        reviewDbStorage.addLike(review.getReviewId(), user.getId());
        assertEquals(1, reviewDbStorage.getById(review.getReviewId()).getUseful());
    }

    @Test
    void deleteLike() {
        userDbStorage.save(user);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        reviewDbStorage.addLike(review.getReviewId(), user.getId());
        assertEquals(1, reviewDbStorage.getById(review.getReviewId()).getUseful());
        reviewDbStorage.deleteLike(review.getReviewId(), user.getId());
        assertEquals(0, reviewDbStorage.getById(review.getReviewId()).getUseful());
    }

    @Test
    void addDislike() {
        userDbStorage.save(user);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        reviewDbStorage.addDislike(review.getReviewId(), user.getId());
        assertEquals(-1, reviewDbStorage.getById(review.getReviewId()).getUseful());
    }

    @Test
    void deleteDislike() {
        userDbStorage.save(user);
        filmDbStorage.save(film);
        reviewDbStorage.create(review);
        reviewDbStorage.addDislike(review.getReviewId(), user.getId());
        assertEquals(-1, reviewDbStorage.getById(review.getReviewId()).getUseful());
        reviewDbStorage.deleteDislike(review.getReviewId(), user.getId());
        assertEquals(0, reviewDbStorage.getById(review.getReviewId()).getUseful());
    }
}