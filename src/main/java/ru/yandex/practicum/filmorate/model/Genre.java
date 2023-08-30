package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Genre {

    private int id;
    @EqualsAndHashCode.Exclude // в сет не должно попасть объектов с одинаковым id
    private String name;
}
