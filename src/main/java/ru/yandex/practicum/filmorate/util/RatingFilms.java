package ru.yandex.practicum.filmorate.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFound.MpaNotFoundException;

import java.util.Arrays;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RatingFilms {

    G("G", 1),
    PG("PG", 2),
    PG_13("PG-13", 3),
    R("R", 4),
    NC_17("NC-17", 5);
    private String name;
    private Integer id;

    public static RatingFilms getById(int id) {
        return Arrays.stream(RatingFilms.values())
                .filter(value -> id == (value.id)).findAny()
                .orElseThrow(() -> new MpaNotFoundException("id не найден"));
    }
}
