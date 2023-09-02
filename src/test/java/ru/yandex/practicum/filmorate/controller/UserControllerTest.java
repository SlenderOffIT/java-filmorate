package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.feed.InMemoryFeedStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    InMemoryUserStorage inMemoryUserStorage;
    InMemoryFeedStorage inMemoryFeedStorage;
    InMemoryFilmStorage inMemoryFilmStorage;
    UserService userService;
    UserController userController;
    User user;
    User user1;
    User user2;

    @BeforeEach
    void createUsers() {
        inMemoryUserStorage = new InMemoryUserStorage();
        inMemoryFeedStorage = new InMemoryFeedStorage();
        userService = new UserService(inMemoryUserStorage, inMemoryFeedStorage, inMemoryFilmStorage);
        userController = new UserController(userService);

        user = new User("asdfg@gmail.com", "Baobab", "Вася", LocalDate.of(1995, 12, 28));
        user1 = new User("qwert@mail.ru", "Grinch", LocalDate.of(2000, 12, 22));
        user2 = new User("rgrgg.yandex.ru", "Blabl", "Сидр", LocalDate.of(2005, 1, 1));
    }

    @Test
    void getUsers() {
        userController.postUser(user);
        userController.postUser(user1);

        Collection<User> allUsers = userController.getUsers();
        System.out.println(allUsers);

        assertEquals(2, inMemoryUserStorage.getStorageUsers().size());
        assertEquals("Grinch", user1.getName());
    }

    @Test
    void postUser() {
        userController.postUser(user);
        userController.postUser(user1);

        assertEquals(2, inMemoryUserStorage.getStorageUsers().size());
        assertEquals(user, inMemoryUserStorage.getStorageUsers().get(1));
        assertEquals(user1, inMemoryUserStorage.getStorageUsers().get(2));
        assertThrows(ValidationException.class, () -> userController.postUser(user2));

    }

    @Test
    void putUser() {
        userController.postUser(user);
        userController.postUser(user1);

        user1.setName("Поттер");
        userController.putUser(user1);

        assertEquals("Поттер", inMemoryUserStorage.getStorageUsers().get(2).getName());
    }
}