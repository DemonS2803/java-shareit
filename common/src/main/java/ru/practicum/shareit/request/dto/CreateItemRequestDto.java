package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CreateItemRequestDto {

    @NotBlank(message = "Item request must contain description")
    private String description;

}
