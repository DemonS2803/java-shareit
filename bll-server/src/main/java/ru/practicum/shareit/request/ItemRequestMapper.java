package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toDto(ItemRequest request) {
        if (request == null) {
            return null;
        }

        List<ItemRequestResponseDto> responses = null;
        if (request.getResponses() != null) {
            responses = request.getResponses().stream()
                    .map(item -> {
                        ItemRequestResponseDto response = new ItemRequestResponseDto();
                        response.setItemId(item.getId());
                        response.setName(item.getName());
                        response.setOwnerId(item.getOwner().getId());
                        return response;
                    })
                    .toList();
        }

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(responses)
                .build();
    }

    public static ItemRequest fromDto(CreateItemRequestDto createDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(createDto.getDescription());
        request.setCreated(LocalDateTime.now());
        return request;
    }

}
