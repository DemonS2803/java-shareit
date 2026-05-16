package ru.practicum.shareit.common.exception;

public class BookingUnavailableException extends RuntimeException {
    public BookingUnavailableException(String message) {
        super(message);
    }
}
