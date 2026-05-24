package ru.practicum.shareit.request;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class ItemRequestResponseServiceImpl implements ItemRequestResponseService {

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public void addItemRequestResponse(Long requestId, Long itemId) {
        Optional<ItemRequest> request = itemRequestRepository.findItemRequestById(requestId);
        log.info("Add response for request {}. Item id = {}", requestId, itemId);
        if (request.isEmpty()) {
            log.warn("Cannot add response for non existing request {}", requestId);
            return;
        }
        itemRequestRepository.addResponseForItemRequest(itemId, requestId);
    }
}
