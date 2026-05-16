package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingDto {

    private Long id;
    private ItemDto item;
    private BookingState status;
    private LocalDateTime start;
    private UserDto booker;
    private LocalDateTime end;

}
