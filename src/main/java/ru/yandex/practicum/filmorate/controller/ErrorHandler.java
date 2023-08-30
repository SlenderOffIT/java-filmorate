package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

/**
 * Обработчик ошибок контроллера.
 * handlerValidationException - ошибка с полем;
 * handleThrowable - ошибка на стороне сервера;
 * handleNotFoundExceptions - если объект не найден.
 */
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationException(final IncorrectParameterException e) {
        return new ErrorResponse(String.format("Ошибка с полем \"%s\".", e.getParameter()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        e.printStackTrace();
        return new ErrorResponse("Произошла непредвиденная ошибка " + e.getClass().getName()
                + " c сообщением " + e.getMessage() + ".");
    }

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class, MpaNotFoundException.class,
            GenreNotFoundException.class, DirectorNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(final Exception e) {
        e.printStackTrace();
        return new ErrorResponse(e.getMessage());
    }
}