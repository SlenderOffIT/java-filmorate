package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.filmorate.util.RatingFilms;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RatingMpa implements Serializable {

    private int id;
    private String name;

    public RatingMpa(Integer id) {
        this.id = id;
        this.name = RatingFilms.getById(id).getName();
    }
}
