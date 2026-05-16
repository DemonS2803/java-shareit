package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateItemDto {

    private Long id;
    @NotEmpty(message = "Item name must be empty")
    private String name;
    @NotEmpty(message = "Item description must be empty")
    private String description;
    private Boolean available;

}
