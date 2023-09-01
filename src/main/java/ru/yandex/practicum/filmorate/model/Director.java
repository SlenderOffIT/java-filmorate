package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * id - суррогатный ключ из БД
 * name - имя режиссера, исключен из equals и hashCode чтобы правильно формировался
 * сет (см. model.film)
 */
@Data
@Validated
public class Director {
    private final int id;
    @NotBlank
    @EqualsAndHashCode.Exclude
    private final String name;
}
