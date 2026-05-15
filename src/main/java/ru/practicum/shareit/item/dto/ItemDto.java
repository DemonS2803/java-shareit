package ru.practicum.shareit.item.dto;

import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;

import lombok.Data;

@Data
public class ItemDto {

    private Long id;
    private UserDto owner;
    private String name;
    private String description;
    private Boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments;

}
