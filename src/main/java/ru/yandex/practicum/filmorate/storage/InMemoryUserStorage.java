package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


/**
 * Данный класс InMemoryUserStorage предназначен для хранения, обновления и поиска пользователей нашего сервиса.
 * getUsers - возвращает список всех пользователей.
 * getUserId - возвращает пользователя по id.
 * postUser - метод для создания пользователя в котором при создании происходит валидация в классе Validation
 * на правильность создания пользователя.
 * changeUser - метод для изменения данных о пользователе.
 */
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private int idUsers = 1;
    @Getter
    private final Map<Integer, User> storageUsers = new HashMap<>();
    @Getter
    @Setter
    private Set<String> userEmail = new HashSet<>();

    public List<User> getUsers() {
        return new ArrayList<>(storageUsers.values());
    }

    public User getUserId(int id) {
        return storageUsers.get(id);
    }

    public User postUser(User user) {
        user.setId(idUsers++);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        storageUsers.put(user.getId(), user);
        userEmail.add(user.getEmail());
        log.debug("Пользователь {} добавлен.", user.getLogin());
        return user;
    }

    public User update(User user) {
        storageUsers.put(user.getId(), user);
        log.debug("Пользователь {} изменен", user.getLogin());
        return user;
    }

    public void deleteFriends(int id, int friendId) {
        User user = getStorageUsers().get(id);
        User friend = getStorageUsers().get(friendId);
        user.getListFriends().remove(friendId);
        friend.getListFriends().remove(id);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}.", id, friendId);
    }

    public boolean isExist(int id) {
        return getStorageUsers().containsKey(id);
    }
}
