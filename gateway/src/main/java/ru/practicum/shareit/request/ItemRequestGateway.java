package ru.practicum.shareit.request;

import jakarta.validation.Valid;

import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.item.dto.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(HttpConstants.ITEM_REQUEST_API_PREFIX)
class ItemRequestGateway {
    private final ItemRequestClient itemRequestClient;


    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestBody @Valid CreateItemRequestDto createDto,
                                             @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.info("Create new item: {}, ownerId: {}", createDto, userId);
        return itemRequestClient.createItemRequest(userId, createDto);
    }


    @GetMapping
    public ResponseEntity<Object> getUserItemRequests(@RequestHeader(HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("Get item requests for user {}", userId);
        return itemRequestClient.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOthersItemRequests(@RequestHeader(HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("User {} requested all items request list", userId);
        return itemRequestClient.getOtherItemRequests(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemRequestById(@PathVariable("id") Long itemRequestId) {
        log.debug("Get detailed info about item request {}", itemRequestId);
        return itemRequestClient.getItemRequestById(itemRequestId);
    }

}
