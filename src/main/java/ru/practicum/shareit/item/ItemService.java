package ru.practicum.shareit.item;

import java.util.List;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

public interface ItemService {

    ItemDto createItem(CreateItemDto dto, Long ownerId);

    ItemDto updateItem(UpdateItemDto dto, Long ownerId);

    ItemDto getItemById(Long id);

    Item getItemEntityById(Long id);

    CommentDto commentItem(CreateCommentDto dto);

    List<ItemDto> searchItems(String text);

    List<ItemDto> getUserItems(Long userId);

}
