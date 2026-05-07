package ru.practicum.shareit.booking;

class BookingUnavailableException extends RuntimeException {
    public BookingUnavailableException(String message) {
        super(message);
    }
}
