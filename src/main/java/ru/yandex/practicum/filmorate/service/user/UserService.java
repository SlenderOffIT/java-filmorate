package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> getUsers();
    User getUserId(int id);
    User postUser(User user);
    User update(User user);
    void deleteUser(int id);
    void deleteFriends(int id, int friendId);
    void addingFriends(int id, int friendId);
    List<User> listFriends(int id);
    List<User> listMutualFriends(int id, int otherId);
    List<Feed> getFeed(int userId);
    List<Film> getRecommendedFilmsForUser(int userId);
}
