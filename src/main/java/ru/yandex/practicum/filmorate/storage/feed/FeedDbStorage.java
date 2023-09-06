package ru.yandex.practicum.filmorate.storage.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Primary
@RequiredArgsConstructor
@Repository
public class FeedDbStorage implements FeedStorage {

    private static final String FEED_TIMESTAMP = "FEED_TIMESTAMP";
    private static final String FEED = "FEED";
    private static final String EVENT_ID = "EVENT_ID";
    private static final String USER_ID = "USER_ID";
    private static final String EVENT_TYPE = "EVENT_TYPE";
    private static final String OPERATION = "OPERATION";
    private static final String ENTITY_ID = "ENTITY_ID";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFeed(int userId, String eventType, String operation, int entityId) {
        if (userId <= 0 || eventType == null || eventType.isEmpty() || operation == null || operation.isEmpty() || entityId <= 0) {
            log.error("Неверные входные данные addFeed: userId = {}, eventType = {}, operation = {}, entityId = {}", userId, eventType, operation, entityId);
            throw new IllegalArgumentException("Invalid input parameters for addFeed");
        }
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(FEED).usingGeneratedKeyColumns(EVENT_ID);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(FEED_TIMESTAMP, timestamp);
        parameters.put(USER_ID, userId);
        parameters.put(EVENT_TYPE, eventType);
        parameters.put(OPERATION, operation);
        parameters.put(ENTITY_ID, entityId);
        jdbcInsert.execute(parameters);
    }

    @Override
    public List<Feed> getFeed(int id) {
        String sqlQuery = "SELECT * FROM FEED where USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::makeFeed, id);
    }

    private Feed makeFeed(ResultSet resultSet, int rowNum) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(FEED_TIMESTAMP);
        Integer userId = resultSet.getInt(USER_ID);
        String eventType = resultSet.getString(EVENT_TYPE);
        String operation = resultSet.getString(OPERATION);
        Integer eventId = resultSet.getInt(EVENT_ID);
        Integer entityId = resultSet.getInt(ENTITY_ID);
        return new Feed(timestamp.getTime(), userId, eventType, operation, eventId, entityId);
    }
}