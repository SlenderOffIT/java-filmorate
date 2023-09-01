package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.AlreadyExists.AlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.NotFound.NotFoundException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder("Аргументы метода не прошли проверку: ");
        e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getObjectName() + " " + error.getField()
                        + ": " + error.getDefaultMessage() + " ")
                .forEach(message::append);
        return new ErrorResponse(message.toString());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        e.printStackTrace();
        return new ErrorResponse("Произошла непредвиденная ошибка " + e.getClass().getName()
                + " c сообщением " + e.getMessage() + ".");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(final NotFoundException e) {
        e.printStackTrace();
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerAlreadyExistsException(final AlreadyExistsException e) {
        return new ErrorResponse(String.format("Ошибка, связанная с наличием объекта в базе: %s.",
                e.getMessage()));
    }
}