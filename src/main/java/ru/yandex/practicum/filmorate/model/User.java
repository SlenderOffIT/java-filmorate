package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> listFriends = new HashSet<>();

    public User() {
    }

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = login;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if (name == null || name.isEmpty()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }

    public Set<Integer> getListFriends() {
        return listFriends;
    }

    public void setListFriends(Integer friends) {
        this.listFriends.add(friends);
    }
}
