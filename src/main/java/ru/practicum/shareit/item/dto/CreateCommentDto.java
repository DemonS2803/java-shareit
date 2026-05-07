package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateCommentDto {

    @NotNull(message = "Comment can not be null")
    @NotBlank(message = "Comment can not be empty")
    private String text;
    private Long userId;
    private Long itemId;

}
