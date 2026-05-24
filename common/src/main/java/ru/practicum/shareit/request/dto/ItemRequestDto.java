package ru.practicum.shareit.request.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;


/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequestDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemRequestResponseDto> items;

}
