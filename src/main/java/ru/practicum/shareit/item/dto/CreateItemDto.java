package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateItemDto {

    @NotEmpty(message = "Item name must be empty")
    @NotNull(message = "Item name mustn't be null")
    private String name;
    @NotEmpty(message = "Item description must be empty")
    @NotNull(message = "Item description mustn't be null")
    private String description;
    @NotNull(message = "New item must have 'available' property")
    private Boolean available;

}
