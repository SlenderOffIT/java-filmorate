package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserStorage {
    List<User> getUsers();

    User getUserId(int id);

    User postUser(User user);

    User update(User user);

    void delete(int id);

    void deleteFriends(int id, int friendId);

    void addingFriends(int id, int friendId);

    Map<Integer, User> getStorageUsers();

    List<User> listFriends(int id);

    List<User> listMutualFriends(int id, int otherId);

    Set<String> getUserEmail();

    boolean isExist(int id);

}
