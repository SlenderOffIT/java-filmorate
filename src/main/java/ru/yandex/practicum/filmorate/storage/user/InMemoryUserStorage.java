package ru.yandex.practicum.filmorate.storage.user;

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

    @Getter
    private final Map<Integer, User> storageUsers = new HashMap<>();
    private int idUsers = 1;
    @Getter
    @Setter
    private Set<String> userEmail = new HashSet<>();

    public List<User> getUsers() {
        return new ArrayList<>(storageUsers.values());
    }

    public Map<Integer, User> getStorageUsers() {
        return storageUsers;
    }

    @Override
    public List<User> listFriends(int id) {
        User user = getStorageUsers().get(id);
        List<User> friends = new ArrayList<>();
        for (int friendId : user.getListFriends()) {
            User friend = getStorageUsers().get(friendId);
            friends.add(friend);
        }
        log.info("Поступил запрос на просмотр списка друзей от пользователя с id {}.", id);
        return friends;
    }

    @Override
    public List<User> listMutualFriends(int id, int otherId) {
        User user = getStorageUsers().get(id);
        User other = getStorageUsers().get(otherId);

        List<Integer> mutualFriendsId = new ArrayList<>(user.getListFriends());
        mutualFriendsId.retainAll(other.getListFriends());

        List<User> mutualFriends = new ArrayList<>();
        for (Integer friends : mutualFriendsId) {
            User outputUser = getStorageUsers().get(friends);
            mutualFriends.add(outputUser);
        }
        return mutualFriends;
    }

    public User getUserId(int id) {
        return storageUsers.get(id);
    }

    public User save(User user) {
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

    @Override
    public void delete(int id) {
        getStorageUsers().remove(id);
    }


    public void deleteFriends(int id, int friendId) {
        User user = getStorageUsers().get(id);
        User friend = getStorageUsers().get(friendId);
        user.getListFriends().remove(friendId);
        friend.getListFriends().remove(id);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}.", id, friendId);
    }

    public void addingFriends(int id, int friendId) {
        User user = getStorageUsers().get(id);
        User friend = getStorageUsers().get(friendId);
        user.setListFriends(friendId);
        friend.setListFriends(id);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", id, friendId);
    }


    public boolean isExist(int id) {
        return getStorageUsers().containsKey(id);
    }
}
