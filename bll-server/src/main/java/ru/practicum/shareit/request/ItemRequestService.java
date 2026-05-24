package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(CreateItemRequestDto createDto, Long userId);

    List<ItemRequestDto> getAllOtherUsersItemRequests(Long userId);

    List<ItemRequestDto> getUserItemRequests(Long userId);

    ItemRequestDto getItemRequestById(Long itemRequestId);

}
