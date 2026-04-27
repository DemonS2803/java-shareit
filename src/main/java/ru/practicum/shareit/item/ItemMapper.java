package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

class ItemMapper {

    public static ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.isAvailable());
        return dto;
    }

    public static Item fromDto(CreateItemDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
//        item.setOwnerId(dto.getAvailable());
//        item.setStatus(ItemStatus.AVAILABLE);
        item.setAvailable(dto.getAvailable());
        return item;
    }

    public static Item fromDto(UpdateItemDto dto) {
        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
//        item.setStatus(dto.getStatus());
        item.setAvailable(dto.getAvailable());
        return item;
    }

}
