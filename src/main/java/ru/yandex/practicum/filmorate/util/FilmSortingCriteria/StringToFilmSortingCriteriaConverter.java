package ru.yandex.practicum.filmorate.util.FilmSortingCriteria;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFound.FilmSortingCriteriaNotFoundException;

@Component
public class StringToFilmSortingCriteriaConverter implements Converter<String, FilmSortingCriteria> {
    @Override
    public FilmSortingCriteria convert(String string) {
        try {
            return FilmSortingCriteria.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new FilmSortingCriteriaNotFoundException(
                    String.format("Критерий сортировки фильмов \"%s\" не найден.", string)
            );
        }
    }
}
