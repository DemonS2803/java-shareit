package ru.practicum.shareit.common.exception;

public class CommentBadRequestException extends RuntimeException {
    public CommentBadRequestException(String message) {
        super(message);
    }
}
