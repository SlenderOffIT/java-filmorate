package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Director> getAllDirectors() {
        log.debug("Отправлен запрос на получение списка режиссеров");
        return jdbcTemplate.query("SELECT * FROM directors", (rs, rowNum) ->
                new Director(rs.getInt("director_id"), rs.getString("director_name")));
    }

    public Director getDirectorById(int id) {
        log.debug("Отправлен запрос на получение режиссера с id = {}", id);
        isExist(id);
        return jdbcTemplate.queryForObject("SELECT * FROM directors WHERE director_id = ?", (rs, rowNum) ->
                new Director(
                        rs.getInt("director_id"),
                        rs.getString("director_name")
                ), id);
    }

    public Director saveDirector(Director director) {
        log.debug("Отправлен запрос на создание режиссера с id = {}", director.getId());
        SimpleJdbcInsert directorInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        int id = directorInsert.executeAndReturnKey(Map.of(
                "director_name", director.getName()
        )).intValue();
        return getDirectorById(id);//возвращаем объект из базы
    }

    public Director updateDirector(Director director) {
        String sql = "MERGE INTO directors (director_id, director_name) VALUES (?,?)";

        log.debug("Отправлен запрос на обновление режиссера с id = {}", director.getId());
        if (isExist(director.getId())) {
            jdbcTemplate.update(sql, director.getId(), director.getName());
        }
        return getDirectorById(director.getId());//возвращаем объект из базы
    }

    public void deleteDirectorById(int id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";

        log.debug("Отправлен запрос на удаление режиссера с id = {}", id);
        jdbcTemplate.update(sql, id);
    }

    public boolean isExist(int id) {
        String sql = "SELECT EXISTS (SELECT director_id FROM directors WHERE director_id = ?)";

        if (!jdbcTemplate.queryForObject(sql, Boolean.class, id)) {
            log.warn("Режиссера с таким id не существует {}", id);
            throw new DirectorNotFoundException(String.format("Режиссер с id = %d не найден", id));
        }
        return true;
    }
}