package ru.practicum.shareit.booking.dto;

import java.util.Optional;

import lombok.Setter;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonInclude;

@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
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
