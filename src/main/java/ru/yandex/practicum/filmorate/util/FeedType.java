package ru.yandex.practicum.filmorate.util;

public enum FeedType {
    FRIEND("FRIEND"),
    LIKE("LIKE"),
    REVIEW("REVIEW"),
    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");

    private String value;

    FeedType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
