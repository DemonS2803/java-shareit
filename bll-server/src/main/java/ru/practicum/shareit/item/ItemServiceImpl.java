package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingItemInfoService;
import ru.practicum.shareit.booking.dto.NearestBookingsDto;
import ru.practicum.shareit.common.exception.ActionNotPermittedForUserException;
import ru.practicum.shareit.common.exception.CommentBadRequestException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.request.ItemRequestResponseService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingItemInfoService bookingItemInfoService;
    private final ItemRequestResponseService itemRequestResponseService;

    @Override
    public ItemDto createItem(CreateItemDto dto, Long ownerId) {
        User owner = userService.getUserEntityById(ownerId);
        log.info("Create new item: {}", dto);
        Item item = ItemMapper.fromDto(dto);
        item.setOwner(owner);
        item = itemRepository.save(item);

        if (dto.getRequestId() != null) {
            log.info("Item {} is created as a request for {}. Adding to responses list", item.getId(), dto.getRequestId());
            itemRequestResponseService.addItemRequestResponse(dto.getRequestId(), item.getId());
        }
        return ItemMapper.toDto(item);
    }

    @Override
    public ItemDto updateItem(UpdateItemDto dto, Long ownerId) {
        Item dbItem = getItemByIdOrThrow(dto.getId());
        if (!Objects.equals(dbItem.getOwner().getId(), ownerId)) {
            throw new ActionNotPermittedForUserException("User " + ownerId + " is not owner of item " + dto.getId());
        }
        log.info("Updating item: {} with ownerId: {}", dto, ownerId);
        if (dto.getName() != null && !dto.getName().isBlank()) {
            dbItem.setName(dto.getName().trim());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            dbItem.setDescription(dto.getDescription().trim());
        }
        if (dto.getAvailable() != null) {
            dbItem.setAvailable(dto.getAvailable());
        }

        return ItemMapper.toDto(itemRepository.save(dbItem));
    }

    @Override
    public ItemDto getItemById(Long id) {
        log.debug("Get item by id: {}", id);
        Item item = getItemByIdOrThrow(id);
        return ItemMapper.toDto(item);
    }

    @Override
    public Item getItemEntityById(Long id) {
        return getItemByIdOrThrow(id);
    }

    private Item getItemByIdOrThrow(Long id) {
        return itemRepository.findItemById(id)
                .orElseThrow(() -> new NotFoundException("Item with id " + id + " not found"));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Search items by text: {}", text);
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        List<ItemDto> items = itemRepository.searchAvailableItems(text).stream()
                .map(ItemMapper::toDto)
                .toList();
        log.info("Found items: {}", items);
        return items;
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        userService.getUserEntityById(userId);
        log.debug("Get user items by ownerId: {}", userId);
        List<Item> userItems = itemRepository.findItemsByOwnerId(userId);
        List<ItemDto> itemDtos = userItems.stream().map(ItemMapper::toDto).toList();
        List<Long> userItemsId = userItems.stream().map(Item::getId).toList();

        Map<Long, NearestBookingsDto> nearestBookingsMap = bookingItemInfoService.getNearestBookingsForItems(userItemsId);
        itemDtos.forEach(item -> {
            NearestBookingsDto nearestDto = nearestBookingsMap.getOrDefault(item.getId(), new NearestBookingsDto());
            if (nearestDto.getPrevious().isPresent()) {
                item.setLastBooking(nearestDto.getPrevious().get().getEnd());
            }
            if (nearestDto.getNext().isPresent()) {
                item.setNextBooking(nearestDto.getNext().get().getStart());
            }
        });
        return itemDtos;
    }

    @Override
    public CommentDto commentItem(CreateCommentDto createDto) {
        User user = userService.getUserEntityById(createDto.getUserId());
        Item item = getItemByIdOrThrow(createDto.getItemId());
        if (!bookingItemInfoService.isUserHadPastBookingForItem(user.getId(), item.getId())) {
            throw new CommentBadRequestException("User not allower to create comment for item " + item.getId());
        }

        log.info("Create comment {}", createDto);
        Comment comment = new Comment(item, user, createDto.getText());
        return ItemMapper.toDto(commentRepository.save(comment));
    }

}
