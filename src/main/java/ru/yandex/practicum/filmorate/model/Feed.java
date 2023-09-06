package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Feed {

    private Long timestamp;
    @Min(1)
    private Integer userId;

    private String eventType;

    private String operation;
    @Min(1)
    private Integer eventId;
    @Min(1)
    private Integer entityId;
}