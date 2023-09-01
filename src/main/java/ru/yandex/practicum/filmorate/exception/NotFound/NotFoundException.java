package ru.yandex.practicum.filmorate.exception.NotFound;

public abstract class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
