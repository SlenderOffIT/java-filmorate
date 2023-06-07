package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Данный класс UserService содержит в себе методы:
 * addingFriends - добавление в друзья пользователей.
 * deleteFriends - удаление пользователей из друзей.
 * listFriends - просмотр списка своих друзей.
 * listMutualFriends - просмотр списка общих друзей с другим пользователем.
 */
@Slf4j
@Service
public class UserService {
    InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addingFriends(int id, int friendId) {
        if (!inMemoryUserStorage.getStorageUsers().containsKey(id)
                || !inMemoryUserStorage.getStorageUsers().containsKey(friendId) || id <= 0 || friendId <= 0) {
            log.debug("Пользователь с id {} пытается добавить в друзья не существующего пользователя", id);
            throw new UserNotFoundException("Вы не можете добавить в друзья не существующего пользователя.");
        }
        User user = inMemoryUserStorage.getStorageUsers().get(id);
        User friend = inMemoryUserStorage.getStorageUsers().get(friendId);
        user.setListFriends(friendId);
        friend.setListFriends(id);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", id, friendId);
    }

    public void deleteFriends(int id, int friendId) {
        if (!inMemoryUserStorage.getStorageUsers().containsKey(id)
                || !inMemoryUserStorage.getStorageUsers().containsKey(friendId) || id <= 0 || friendId <= 0) {
            log.debug("Пользователь с id {} пытается удалить из друзей не существующего пользователя", id);
            throw new UserNotFoundException("Удалять не кого, такого пользователя не существует.");
        }
        User user = inMemoryUserStorage.getStorageUsers().get(id);
        User friend = inMemoryUserStorage.getStorageUsers().get(friendId);
        user.getListFriends().remove(friendId);
        friend.getListFriends().remove(id);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}.", id, friendId);
    }

    public List<User> listFriends(@PathVariable int id) {
        User user = inMemoryUserStorage.getStorageUsers().get(id);
        List<User> friends = new ArrayList<>();
        for (int friendId : user.getListFriends()) {
            User friend = inMemoryUserStorage.getStorageUsers().get(friendId);
            friends.add(friend);
        }
        log.info("Поступил запрос на просмотр списка друзей от пользователя с id {}.", id);
        return friends;
    }

    public List<User> listMutualFriends(int id, int otherId) {
        if (!inMemoryUserStorage.getStorageUsers().containsKey(otherId)) {
            log.debug("Пользователь с id {} запросил список общих друзей с не существующим пользователем с id {}.", id, otherId);
            throw new UserNotFoundException("Удалять не кого, такого пользователя не существует.");
        }
        User user = inMemoryUserStorage.getStorageUsers().get(id);
        User other = inMemoryUserStorage.getStorageUsers().get(otherId);

        List<Integer> mutualFriendsId = new ArrayList<>(user.getListFriends());
        mutualFriendsId.retainAll(other.getListFriends());

        List<User> mutualFriends = new ArrayList<>();
        for (Integer friends: mutualFriendsId) {
            User outputUser = inMemoryUserStorage.getStorageUsers().get(friends);
            mutualFriends.add(outputUser);
        }
        log.info("Пользователь с id {} запросил список общих друзей с пользователем id {}.", id, otherId);
        return mutualFriends;
    }
}
