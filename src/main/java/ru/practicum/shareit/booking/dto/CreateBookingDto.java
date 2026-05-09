package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingDto {

    @NotNull(message = "Booking must have requested item id")
    private Long itemId;
    @NotNull(message = "Booking must have start time")
    @FutureOrPresent(message = "Booking mustn't start in past")
    private LocalDateTime start;
    @NotNull(message = "Booking must have end time")
    @Future(message = "Booking end time must be future")
    private LocalDateTime end;


    @AssertTrue(message = "Booking must have start before end")
    public boolean isStartBeforeEnd() {
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }

}
