package ru.practicum.shareit.common.exception;

public class ActionNotPermittedForUserException extends RuntimeException {
    public ActionNotPermittedForUserException(String message) {
        super(message);
    }
}
