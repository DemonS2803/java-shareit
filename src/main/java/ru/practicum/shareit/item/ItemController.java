package ru.practicum.shareit.item;

import java.util.List;

import jakarta.validation.Valid;

import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("")
    public ItemDto createItem(@RequestBody @Valid CreateItemDto createDto,
                              @RequestHeader(value = HttpConstants.SHARER_USER_PARAM) Long userId) {
        log.info("Create new item: {}, ownerId: {}", createDto, userId);
        return itemService.createItem(createDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody UpdateItemDto updateDto,
                              @RequestHeader(value = HttpConstants.SHARER_USER_PARAM) Long userId) {
        updateDto.setId(itemId);
        log.info("Update item: {}, ownerId: {}", updateDto, userId);
        return itemService.updateItem(updateDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.debug("Get item: {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping("")
    public List<ItemDto> getUserItems(@RequestHeader(value = HttpConstants.SHARER_USER_PARAM) Long userId) {
        log.debug("Get user items: {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam(value = "text") String text) {
        log.debug("Search item by text: {}", text);
        return itemService.searchItems(text);
    }

}
