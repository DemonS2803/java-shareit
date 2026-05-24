package ru.practicum.shareit.request;

import jakarta.validation.Valid;
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
@RequestMapping(path = "/requests")
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
    public List<ItemRequestDto> getUserItemRequests(@RequestHeader(HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("Get item requests for user {}", userId);
        return itemRequestService.getUserItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOthersItemRequests(@RequestHeader(HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("User {} requested all items request list", userId);
        return itemRequestService.getAllOtherUsersItemRequests(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequestById(@PathVariable("id") Long itemRequestId) {
        log.debug("Get detailed info about item request {}", itemRequestId);
        return itemRequestService.getItemRequestById(itemRequestId);
    }

}
