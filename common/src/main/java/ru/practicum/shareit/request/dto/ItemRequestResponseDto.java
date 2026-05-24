package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemRequestResponseDto {

    private Long itemId;
    private Long ownerId;
    private String name;

}
