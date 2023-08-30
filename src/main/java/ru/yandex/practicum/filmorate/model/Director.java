package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
public class Director {
    private final int id;
    @NotBlank
    @EqualsAndHashCode.Exclude // чтобы в сет не попало 2 объекта с одинаковым id
    private final String name;
}
