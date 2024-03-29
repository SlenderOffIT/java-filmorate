package ru.yandex.practicum.filmorate.service.ratingMpa;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFound.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.RatingMpaStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RatingMpaServiceImpl implements RatingMpaService {

    RatingMpaStorage ratingMpaStorage;

    @Override
    public List<RatingMpa> getAllRatingMpa() {
        log.debug("Обрабатываем запрос на просмотр всех рейтингов МРА.");
        return ratingMpaStorage.getAllRatingMpa();
    }

    @Override
    public RatingMpa getRantingMpaById(int id) {
        if (ratingMpaStorage.isExist(id)) {
            return ratingMpaStorage.getRantingMpaById(id);
        } else {
            log.debug("Рейтинг MPA с таким id не существующего {}", id);
            throw new MpaNotFoundException(String.format("Рейтинг MPA с таким id %d не существует.", id));
        }
    }
}
