package ru.yandex.practicum.filmorate.storage.feed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFeedStorage implements FeedStorage {

    private final Map<Integer, Feed> storageFeed = new HashMap<>();
    private int feedEventId = 1;

    @Override
    public void addFeed(int userId, String eventType, String operation, int entityId) {
        if (userId <= 0 || eventType == null || eventType.isEmpty() || operation == null || operation.isEmpty() || entityId <= 0) {
            log.error("Неверные входные данные addFeed: userId = {}, eventType = {}, operation = {}, entityId = {}", userId, eventType, operation, entityId);
            throw new IllegalArgumentException("Invalid input parameters for addFeed");
        }
        Feed newEvent = new Feed(System.currentTimeMillis(), userId, eventType, operation, feedEventId++, entityId);
        storageFeed.put(newEvent.getEventId(), newEvent);
        log.debug("Добавлено событие в ленту для пользователя с ID {}: {}", userId, newEvent);
    }


    @Override
    public List<Feed> getFeed(int userId) {
        return storageFeed.values().stream()
                .filter(feed -> feed.getUserId() == userId)
                .collect(Collectors.toList());
    }
}