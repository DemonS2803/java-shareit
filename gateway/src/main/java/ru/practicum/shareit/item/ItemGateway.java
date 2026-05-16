package ru.practicum.shareit.item;

import jakarta.validation.Valid;

import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.item.dto.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(HttpConstants.ITEM_API_PREFIX)
class ItemGateway {
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody @Valid CreateItemDto createDto,
                                             @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.info("Create new item: {}, ownerId: {}", createDto, userId);
        return itemClient.createItem(userId, createDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                              @RequestBody UpdateItemDto updateDto,
                              @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        updateDto.setId(itemId);
        log.info("Update item: {}, ownerId: {}", updateDto, userId);
        return itemClient.updateItem(userId, itemId, updateDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable Long itemId,
                                          @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("Get item: {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("Get user items: {}", userId);
        return itemClient.getItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(
            @RequestParam(value = HttpConstants.SEARCH_ITEM_TEXT_PARAM) String text,
            @RequestHeader(value = HttpConstants.SHARER_USER_HEADER, required = false) Long userId) {
        log.debug("Search item by text: {}", text);
        return itemClient.searchItemsByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> commentItem(
            @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody @Valid CreateCommentDto createCommentDto) {
        log.info("User {} comment item {} with text: {}", userId, itemId, createCommentDto.getText());
        return itemClient.commentItem(userId, itemId, createCommentDto);
    }
}
