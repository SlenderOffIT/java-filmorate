package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.filmorate.util.Validations.validation;

/**
 * Данный класс UserService содержит в себе методы:
 * addingFriends - добавление в друзья пользователей.
 * deleteFriends - удаление пользователей из друзей.
 * listFriends - просмотр списка своих друзей.
 * listMutualFriends - просмотр списка общих друзей с другим пользователем.
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    UserStorage userStorage;

    public List<User> getUsers() {
        log.debug("Поступил запрос на просмотр списка всех пользователей");
        return userStorage.getUsers();
    }

    public User getUserId(int id) {
        log.debug("Поступил запрос на просмотр пользователя с id {}", id);
        if (!userStorage.isExist(id) || id <= 0) {
            log.debug("По данному запросу, пользователь с id {} не найден", id);
            throw new UserNotFoundException(String.format("Не корректный id %d пользователя", id));
        }
        return userStorage.getUserId(id);
    }

    public User postUser(User user) {
        log.debug("Поступил запрос на создание пользователя с id {}", user.getId());
        if (userStorage.getUserEmail().contains(user.getEmail())) {
            log.debug("Попытка создать пользователя по уже существующему email {}", user.getEmail());
            throw new ValidationException(String.format("Пользователь с таким email %s уже существует.", user.getEmail()));
        }
        validation(user);
        return userStorage.postUser(user);
    }

    public User update(User user) {
        log.debug("Поступил запрос на изменение пользователя с id {}", user.getId());
        if (!userStorage.isExist(user.getId())) {
            log.debug("Не корректный пользователь с email {}", user.getLogin());
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", user.getId()));
        }
        validation(user);
        return userStorage.update(user);
    }

    public void deleteFriends(int id, int friendId) {
        log.debug("Поступил запрос на удаление пользователя с id {} из друзей у пользователя с id {}", friendId, id);
        if (!userStorage.isExist(id) || id <= 0) {
            log.debug("Пользователь с id {} не существует", id);
            throw new UserNotFoundException("Такого пользователя не существует.");
        } else if (!userStorage.isExist(friendId) || friendId <= 0) {
            log.debug("Пользователь с id {} пытается удалить из друзей не существующего пользователя", id);
            throw new UserNotFoundException("Удалять не кого, такого пользователя не существует.");
        }
        userStorage.deleteFriends(id, friendId);
    }

    public void addingFriends(int id, int friendId) {
        if (!userStorage.getStorageUsers().containsKey(id)
                || !userStorage.getStorageUsers().containsKey(friendId) || id <= 0 || friendId <= 0) {
            log.debug("Пользователь с id {} пытается добавить в друзья не существующего пользователя", id);
            throw new UserNotFoundException("Вы не можете добавить в друзья не существующего пользователя.");
        }
        User user = userStorage.getStorageUsers().get(id);
        User friend = userStorage.getStorageUsers().get(friendId);
        user.setListFriends(friendId);
        friend.setListFriends(id);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", id, friendId);
    }

    public List<User> listFriends(@PathVariable int id) {
        User user = userStorage.getStorageUsers().get(id);
        List<User> friends = new ArrayList<>();
        for (int friendId : user.getListFriends()) {
            User friend = userStorage.getStorageUsers().get(friendId);
            friends.add(friend);
        }
        log.info("Поступил запрос на просмотр списка друзей от пользователя с id {}.", id);
        return friends;
    }

    public List<User> listMutualFriends(int id, int otherId) {
        if (!userStorage.isExist(otherId)) {
            log.debug("Пользователь с id {} запросил список общих друзей с не существующим пользователем с id {}.", id, otherId);
            throw new UserNotFoundException("Удалять не кого, такого пользователя не существует.");
        }
        User user = userStorage.getStorageUsers().get(id);
        User other = userStorage.getStorageUsers().get(otherId);

        List<Integer> mutualFriendsId = new ArrayList<>(user.getListFriends());
        mutualFriendsId.retainAll(other.getListFriends());

        List<User> mutualFriends = new ArrayList<>();
        for (Integer friends: mutualFriendsId) {
            User outputUser = userStorage.getStorageUsers().get(friends);
            mutualFriends.add(outputUser);
        }
        log.info("Пользователь с id {} запросил список общих друзей с пользователем id {}.", id, otherId);
        return mutualFriends;
    }
}
