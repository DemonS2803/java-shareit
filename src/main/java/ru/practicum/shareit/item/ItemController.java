package ru.practicum.shareit.item;

import java.util.List;

import jakarta.validation.Valid;

import ru.practicum.shareit.common.web.dto.ErrorResponseDto;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("")
    public ItemDto createItem(@RequestBody @Valid CreateItemDto createDto,
                              @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.info("Create new item: {}, ownerId: {}", createDto, userId);
        return itemService.createItem(createDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody UpdateItemDto updateDto,
                              @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        updateDto.setId(itemId);
        log.info("Update item: {}, ownerId: {}", updateDto, userId);
        return itemService.updateItem(updateDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        log.debug("Get item: {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping("")
    public List<ItemDto> getUserItems(@RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("Get user items: {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemByText(@RequestParam(value = "text") String text) {
        log.debug("Search item by text: {}", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto commentItem(
            @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId,
            @PathVariable("itemId") Long itemId,
            @RequestBody @Valid CreateCommentDto createCommentDto) {
        log.info("User {} comment item {} with text: {}", userId, itemId, createCommentDto.getText());
        // Насколько правильно так подавать параметры? Или лучше 3 параметра
        // Вроде как чем в методе меньше входных параметров, тем лучше?
        createCommentDto.setItemId(itemId);
        createCommentDto.setUserId(userId);
        return itemService.commentItem(createCommentDto);
    }

    @ExceptionHandler(CommentBadRequestException.class)
    public ResponseEntity<?> handleCommentBadRequest(CommentBadRequestException ex) {
        log.error("User couldn't create comment");
        return ResponseEntity.status(400)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

}
