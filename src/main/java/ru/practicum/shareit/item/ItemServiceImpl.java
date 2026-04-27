package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.practicum.shareit.common.exception.ActionNotPermittedForUserException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto createItem(CreateItemDto dto, Long ownerId) {
        userService.getUserById(ownerId);
        log.info("Create new item: {}", dto);
        Item item = ItemMapper.fromDto(dto);
        item.setOwnerId(ownerId);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(UpdateItemDto dto, Long ownerId) {
        Item dbItem = getItemOrThrow(dto.getId());
        if (!Objects.equals(dbItem.getOwnerId(), ownerId)) {
            throw new ActionNotPermittedForUserException("User " + ownerId + " is not owner of item " + dto.getId());
        }
        log.info("Updating item: {} with ownerId: {}", dto, ownerId);
        if (dto.getName() != null) {
            dbItem.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            dbItem.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            dbItem.setAvailable(dto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.update(dbItem));
    }

    @Override
    public ItemDto getItem(Long id) {
        log.debug("Get item by id: {}", id);
        Item item = getItemOrThrow(id);
        return ItemMapper.toDto(item);
    }

    private Item getItemOrThrow(Long id) {
        return itemRepository.findItemById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.debug("Search items by text: {}", text);
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        userService.getUserById(userId);
        log.debug("Get user items by ownerId: {}", userId);
        return itemRepository.findItemsByOwnerId(userId).stream()
                .map(ItemMapper::toDto)
                .toList();
    }

}
