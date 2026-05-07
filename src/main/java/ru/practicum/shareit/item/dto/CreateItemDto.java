package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemDto {

    @NotBlank(message = "Item name mustn't be empty")
    private String name;
    @NotBlank(message = "Item description mustn't be empty")
    private String description;
    @NotNull(message = "New item must have 'available' property")
    private Boolean available;

}
