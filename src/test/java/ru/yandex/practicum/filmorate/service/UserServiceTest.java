package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    InMemoryUserStorage inMemoryUserStorage;
    UserService userService;
    UserController userController;
    User user;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    void createUsers() {
        inMemoryUserStorage = new InMemoryUserStorage();
        userService = new UserService(inMemoryUserStorage);
        userController = new UserController(inMemoryUserStorage, userService);

        user = new User("asdfg@gmail.com", "Baobab", "Вася", LocalDate.of(1995, 12, 28));
        user1 = new User("qwert@mail.ru", "Grinch", LocalDate.of(2000, 12, 22));
        user2 = new User("rgrgg.yandex.ru", "Blabl", "Сидр", LocalDate.of(2005, 1, 1));
        user3 = new User("mnbbvct@mail.ru", "Grinch", "Aria", LocalDate.of(1990, 4, 5));
    }

    @Test
    void addingFriends() {
        User userCreate = userController.postUser(user);
        User userCreate1 = userController.postUser(user1);

        userService.addingFriends(userCreate.getId(), userCreate1.getId());

        assertEquals(1, userCreate.getListFriends().size());
        assertEquals(1, userCreate1.getListFriends().size());
    }

    @Test
    void deleteFriends() {
        User userCreate = userController.postUser(user);
        User userCreate1 = userController.postUser(user1);

        userService.addingFriends(userCreate.getId(), userCreate1.getId());
        userService.deleteFriends(userCreate.getId(), userCreate1.getId());

        assertEquals(0, userCreate.getListFriends().size());
        assertEquals(0, userCreate1.getListFriends().size());
    }

    @Test
    void listFriends() {
        User userCreate = userController.postUser(user);
        User userCreate1 = userController.postUser(user1);

        userService.addingFriends(userCreate.getId(), userCreate1.getId());
        List<User> friends = userService.listFriends(1);

        assertEquals(1, friends.size());
        assertEquals(2, friends.get(0).getId());
    }

    @Test
    void listMutualFriends() {
        User userCreate = userController.postUser(user);
        User userCreate1 = userController.postUser(user1);
        User userCreate2 = userController.postUser(user3);

        userService.addingFriends(userCreate.getId(), userCreate1.getId());
        userService.addingFriends(userCreate2.getId(), userCreate1.getId());

        List<User> mutual = userService.listMutualFriends(userCreate.getId(), userCreate2.getId());

        assertEquals(1, mutual.size());
        assertEquals(2, mutual.get(0).getId());
    }
}