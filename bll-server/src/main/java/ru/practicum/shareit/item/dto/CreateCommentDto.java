package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CreateCommentDto {

    @NotBlank(message = "Comment can not be empty")
    private String text;
    private Long userId;
    private Long itemId;

}
