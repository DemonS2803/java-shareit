package ru.practicum.shareit.booking;

import java.util.Optional;

public enum BookingRequestState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static Optional<BookingRequestState> from(String value) {
        return Optional.ofNullable(BookingRequestState.valueOf(value.toUpperCase()));
    }
}
