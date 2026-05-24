package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(CreateItemRequestDto createDto, Long userId);

    List<ItemRequestDto> getAllOtherUsersItemRequests(Long userId, int from, int size);

    List<ItemRequestDto> getUserItemRequests(Long userId, int from, int size);

    ItemRequestDto getItemRequestById(Long itemRequestId);

}
