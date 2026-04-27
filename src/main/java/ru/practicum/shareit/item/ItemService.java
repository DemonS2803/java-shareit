package ru.practicum.shareit.item;

import java.util.List;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

public interface ItemService {

    ItemDto createItem(CreateItemDto dto, Long ownerId);

    ItemDto updateItem(UpdateItemDto dto, Long ownerId);

    ItemDto getItem(Long id);

    List<ItemDto> searchItems(String text);

    List<ItemDto> getUserItems(Long userId);

}
