package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemDto {

    private Long id;
    @NotEmpty(message = "Item name must be empty")
    private String name;
    @NotEmpty(message = "Item description must be empty")
    private String description;
    private Boolean available;

}
