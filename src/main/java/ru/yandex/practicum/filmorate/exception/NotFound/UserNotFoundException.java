package ru.yandex.practicum.filmorate.exception.NotFound;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
