package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query("SELECT u.id, u.name, u.email, u.login, u.birthday, f.id_friend " +
                "FROM \"USER\" u " +
                "LEFT JOIN FRIENDS f ON u.id = f.id_user " +
                "GROUP BY u.id, f.ID_FRIEND", listUserRowMapper()).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public User getUserId(int id) {
        String sql = "SELECT u.id, u.name, u.email, u.login, u.birthday, f.id_friend " +
                "FROM \"USER\" u " +
                "LEFT JOIN friends f ON u.id = f.id_user " +
                "WHERE u.id = ?";

        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
            int userId = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            String login = rs.getString("login");
            LocalDate birthday = rs.getDate("birthday").toLocalDate();

            User user = new User(userId, email, login, name, birthday);
            do {
                int friend = rs.getInt("id_friend");
                user.setListFriends(friend);
            } while (rs.next());
            return user;
        }, id);

        return users.isEmpty() ? null : users.get(0);
    }

    @Override
    public User postUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("\"USER\"")
                .usingGeneratedKeyColumns("id")
                .usingColumns("login", "name", "email", "birthday");

        Map<String, Object> params = new HashMap<>();
        params.put("login", user.getLogin());
        params.put("name", user.getName());
        params.put("email", user.getEmail());
        params.put("birthday", java.sql.Date.valueOf(user.getBirthday()));

        Number id = simpleJdbcInsert.executeAndReturnKey(params);
        user.setId(id.intValue());

        log.debug("Пользователь {} добавлен.", user.getLogin());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE \"USER\" SET id=?, name=?, email=?, login=?, birthday=? " +
                     "WHERE id = ?";
        jdbcTemplate.update(sql, user.getId(), user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        log.debug("Пользователь {} изменен", user.getLogin());
        return user;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM \"USER\" WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteFriends(int idUser, int friendId) {
        String sql = "DELETE FROM friends " +
                     "WHERE id_user = ? AND id_friend = ?";
        jdbcTemplate.update(sql, idUser, friendId);
    }

    @Override
    public void addingFriends(int idUser, int friendId) {
        String sql = "INSERT INTO friends (id_user, id_friend) " +
                     "VALUES (?, ?)";
        jdbcTemplate.update(sql, idUser, friendId);
    }

    @Override
    public Map<Integer, User> getStorageUsers() {
        Map<Integer, User> userMap = new HashMap<>();

        jdbcTemplate.query(("SELECT * FROM \"USER\""), rs -> {
            do {
                int userId = rs.getInt("id");
                if (!userMap.containsKey(userId)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setLogin(rs.getString("login"));
                    user.setBirthday(rs.getDate("birthday").toLocalDate());
                    userMap.put(user.getId(), user);
                }
            } while (rs.next());
        });
        return userMap;
    }

    @Override
    public List<User> listFriends(int id) {
        return jdbcTemplate.query("SELECT u.id, u.NAME, u.EMAIL, u.LOGIN, u.BIRTHDAY, fr.ID_FRIEND " +
                "FROM (SELECT id_friend " +
                      "FROM FRIENDS " +
                      "WHERE ID_USER = ?) AS f " +
                "LEFT JOIN \"USER\" u ON f.id_friend = u.ID " +
                "LEFT JOIN FRIENDS fr on f.id_friend = FR.ID_USER", listUserRowMapper(), id).stream().findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<User> listMutualFriends(int id, int otherId) {
        return jdbcTemplate.query("SELECT u.ID, u.NAME, u.EMAIL, u.LOGIN, u.BIRTHDAY " +
                "FROM FRIENDS f " +
                "JOIN FRIENDS f2 ON f2.ID_USER = ? " +
                "AND f2.ID_FRIEND = f.ID_FRIEND " +
                "JOIN \"USER\" u ON u.ID  = f.ID_FRIEND " +
                "WHERE f.ID_USER  = ?", (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }, id, otherId);
    }

    @Override
    public Set<String> getUserEmail() {
        Set<String> sqlEmail = new HashSet<>();
        String sql = "SELECT email FROM \"USER\"";
        jdbcTemplate.query(sql, (ResultSet rs) -> {
            while (rs.next()) {
                String email = rs.getString("email");
                sqlEmail.add(email);
            }
        });
        return sqlEmail;
    }

    @Override
    public boolean isExist(int id) {
        String checkId = "SELECT COUNT(id) FROM \"USER\" WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(checkId, Integer.class, id);
        if (count < 1) {
            return false;
        } else {
            return true;
        }
    }

    private RowMapper<List<User>> listUserRowMapper() {
        return ((rs, rowNum) -> {
            List<User> users = new ArrayList<>();

            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());

            do {
                if (user.getId() == rs.getInt("id")) {
                    if (rs.getInt("id_friend") > 0) {
                        user.setListFriends(rs.getInt("id_friend"));
                    }
                } else {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setLogin(rs.getString("login"));
                    user.setBirthday(rs.getDate("birthday").toLocalDate());
                    if (rs.getInt("id_friend") > 0) {
                        user.setListFriends(rs.getInt("id_friend"));
                    }
                }
                if (!users.stream()
                        .map(User::getId)
                        .collect(Collectors.toList()).contains(user.getId())) {
                    users.add(user);
                }
            } while (rs.next());
            return users;
        });
    }
}
