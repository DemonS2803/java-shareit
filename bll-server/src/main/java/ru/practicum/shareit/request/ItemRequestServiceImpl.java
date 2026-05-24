package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class ItemRequestServiceImpl implements ItemRequestService {

    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;

    private ItemRequest getItemRequestByidOrThrow(Long itemRequestId) {
        return itemRequestRepository.findItemRequestById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Item request with id " + itemRequestId + " is not found"));
    }

    @Override
    public ItemRequestDto createItemRequest(CreateItemRequestDto createDto, Long userId) {
        log.info("User {} wants to create request: {}", userId, createDto.getDescription());
        User user = userService.getUserEntityById(userId);

        ItemRequest request = ItemRequestMapper.fromDto(createDto);
        request.setUser(user);
        request = itemRequestRepository.save(request);
        log.info("User {} created item request {}", userId, request.getId());
        return ItemRequestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllOtherUsersItemRequests(Long userId) {
        log.debug("User {} want to get global requests list", userId);
        List<ItemRequest> requests = itemRequestRepository.findByUserIdIsNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getUserItemRequests(Long userId) {
        log.debug("User {} wants to get his item requests", userId);
        List<ItemRequest> requests = itemRequestRepository.findByUserIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .toList();
    }

    @Override
    public ItemRequestDto getItemRequestById(Long itemRequestId) {
        ItemRequest request = getItemRequestByidOrThrow(itemRequestId);
        return ItemRequestMapper.toDto(request);
    }
}
