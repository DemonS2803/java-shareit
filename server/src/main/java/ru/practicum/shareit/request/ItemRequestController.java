package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = HttpConstants.ITEM_REQUEST_API_PREFIX)
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(
            @RequestBody @Valid CreateItemRequestDto createItemDto,
            @RequestHeader(HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("Create item request: {}", createItemDto);
        return itemRequestService.createItemRequest(createItemDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserItemRequests(
            @RequestHeader(HttpConstants.SHARER_USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = HttpConstants.PAGINATION_FROM_PARAM, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = HttpConstants.PAGINATION_SIZE_PARAM, defaultValue = "10") Integer size) {
        log.debug("Get item requests for user {}", userId);
        return itemRequestService.getUserItemRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOthersItemRequests(
            @RequestHeader(HttpConstants.SHARER_USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = HttpConstants.PAGINATION_FROM_PARAM, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = HttpConstants.PAGINATION_SIZE_PARAM, defaultValue = "10") Integer size) {
        log.debug("User {} requested all items request list", userId);
        return itemRequestService.getAllOtherUsersItemRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequestById(@PathVariable("id") Long itemRequestId) {
        log.debug("Get detailed info about item request {}", itemRequestId);
        return itemRequestService.getItemRequestById(itemRequestId);
    }

}
