package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private User user;
    private User user1;
    private User user2;

    @BeforeEach
    public void create () {
        user = new User("asdfg@gmail.com", "Baobab", "Вася", LocalDate.of(1995, 12, 28));
        user1 = new User("qwert@mail.ru", "Grinch", LocalDate.of(2000, 12, 22));
        user2 = new User("mnbbvct@mail.ru", "Grinch", "Aria", LocalDate.of(1990, 4, 5));
    }

    @Test
    public void testCreatUser() {
        User addUser = userStorage.postUser(user);
        assertEquals(addUser, userStorage.getUsers().get(0));
    }

    @Test
    public void testGetUsers() {
        User addUser = userStorage.postUser(user);
        assertEquals(addUser, userStorage.getUsers().get(0));
        assertEquals(1, userStorage.getUsers().size());
    }

    @Test
    public void testGetUsersById() {
        User addUser = userStorage.postUser(user);
        assertEquals(1, addUser.getId());
    }

    @Test
    public void testUpdate() {
        User addUser = userStorage.postUser(user);
        User updadeUser  = userStorage.update(new User(1, "rgrgg.yandex.ru", "Blabl", "Сидр",
                LocalDate.of(2005, 1, 1)));

        assertEquals(1, updadeUser.getId());
        assertEquals("rgrgg.yandex.ru", updadeUser.getEmail());
        assertEquals(updadeUser, userStorage.getUsers().get(0));
    }

    @Test
    public void testDelete() {
        User addUser = userStorage.postUser(user);
        userStorage.delete(addUser.getId());

        assertTrue(userStorage.getUsers().isEmpty());
    }

    @Test
    public void testAddingFriends() {
        User addUser = userStorage.postUser(user);
        User addUser1 = userStorage.postUser(user1);
        userStorage.addingFriends(addUser.getId(), addUser1.getId());
        User addFriend = userStorage.getUserId(addUser.getId());

        assertEquals(1, addFriend.getListFriends().size());
    }

    @Test
    public void testDeleteFriends() {
        User addUser = userStorage.postUser(user);
        User addUser1 = userStorage.postUser(user1);
        userStorage.addingFriends(addUser.getId(), addUser1.getId());
        User addFriend = userStorage.getUserId(addUser.getId());

        userStorage.deleteFriends(addFriend.getId(), addUser1.getId());
        List<User> friend = userStorage.listFriends(addFriend.getId());
        assertTrue(friend.isEmpty());
    }

    @Test
    public void testListFriends() {
        User addUser = userStorage.postUser(user);
        User addUser1 = userStorage.postUser(user1);
        userStorage.addingFriends(addUser.getId(), addUser1.getId());
        User addFriend = userStorage.getUserId(addUser.getId());
        List<User> friend = userStorage.listFriends(1);

        assertEquals(1, friend.size());
        assertEquals(2, friend.get(0).getId());
        assertEquals(addUser1, friend.get(0));
    }

    @Test
    public void testListMutualFriends() {
        User addUser = userStorage.postUser(user);
        User addUser1 = userStorage.postUser(user1);
        User addUser2 = userStorage.postUser(user2);

        userStorage.addingFriends(addUser.getId(), addUser1.getId());
        userStorage.addingFriends(addUser2.getId(), addUser1.getId());
        User addFriend = userStorage.getUserId(addUser.getId());
        User addFriend2 = userStorage.getUserId(addUser2.getId());

        List<User> friends = userStorage.listMutualFriends(addFriend.getId(), addFriend2.getId());
        assertEquals(1, friends.size());
        assertEquals(addUser1, friends.get(0));
    }
}