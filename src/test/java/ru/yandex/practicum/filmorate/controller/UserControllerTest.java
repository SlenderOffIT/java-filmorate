package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController;
    User user;
    User user1;
    User user2;

    @BeforeEach
    void createUsers() {
        userController = new UserController();
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

        assertEquals(2, userController.getStorageUsers().size());
        assertEquals("Grinch", user1.getName());
    }

    @Test
    void postUser() {
        userController.postUser(user);
        userController.postUser(user1);

        assertEquals(2, userController.getStorageUsers().size());
        assertEquals(user, userController.getStorageUsers().get(1));
        assertEquals(user1, userController.getStorageUsers().get(2));
        assertThrows(ValidationException.class, () -> userController.postUser(user2));

    }

    @Test
    void changeUser() {
        userController.postUser(user);
        userController.postUser(user1);

        user1.setName("Поттер");
        userController.changeUser(user1);

        assertEquals("Поттер", userController.getStorageUsers().get(2).getName());
    }
}