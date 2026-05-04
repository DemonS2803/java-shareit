package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateItemDto {

    @NotBlank(message = "Item name mustn't be empty")
    private String name;
    @NotBlank(message = "Item description mustn't be empty")
    private String description;
    @NotNull(message = "New item must have 'available' property")
    private Boolean available;

}
