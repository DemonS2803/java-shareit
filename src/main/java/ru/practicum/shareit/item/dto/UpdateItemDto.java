package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UpdateItemDto {

    private Long id;
    @NotEmpty(message = "Item name must be empty")
    private String name;
    @NotEmpty(message = "Item description must be empty")
    private String description;
    private Boolean available;

}
