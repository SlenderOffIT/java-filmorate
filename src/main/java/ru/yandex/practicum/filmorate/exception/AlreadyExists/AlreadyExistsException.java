package ru.yandex.practicum.filmorate.exception.AlreadyExists;

public abstract class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
