package ru.practicum.shareit.booking.dto;

import java.util.Optional;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class NearestBookingsDto {

    private BookingDto previous;
    private BookingDto next;

    public Optional<BookingDto> getPrevious() {
        return Optional.ofNullable(previous);
    }

    public Optional<BookingDto> getNext() {
        return Optional.ofNullable(next);
    }

}
