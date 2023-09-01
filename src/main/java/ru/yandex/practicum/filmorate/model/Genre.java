package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * id - суррогатный ключ из БД
 * name - имя жанра, исключен из equals и hashCode чтобы правильно формировался
 * сет (см. model.film)
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Genre {
    private int id;
    @EqualsAndHashCode.Exclude
    private String name;
}
