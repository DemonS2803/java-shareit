package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestGatewayTest {

    @Mock
    ItemRequestClient itemRequestClient;

    @InjectMocks
    ItemRequestGateway itemRequestGateway;

    @Test
    void createItemRequest_delegatesToClient() {
        long userId = 77L;
        CreateItemRequestDto dto = mock(CreateItemRequestDto.class);
        ResponseEntity<Object> responseMock = ResponseEntity.ok().build();

        when(itemRequestClient.createItemRequest(userId, dto)).thenReturn(responseMock);

        ResponseEntity<Object> result = itemRequestGateway.createItemRequest(dto, userId);

        assertEquals(responseMock, result);
        verify(itemRequestClient).createItemRequest(userId, dto);
    }

    @Test
    void getUserItemRequests_delegatesToClient() {
        long userId = 24L;
        int from = 2;
        int size = 15;
        ResponseEntity<Object> responseMock = ResponseEntity.ok().build();

        when(itemRequestClient.getUserItemRequests(userId, from, size)).thenReturn(responseMock);

        ResponseEntity<Object> result = itemRequestGateway.getUserItemRequests(userId, from, size);

        assertEquals(responseMock, result);
        verify(itemRequestClient).getUserItemRequests(userId, from, size);
    }

    @Test
    void getOthersItemRequests_delegatesToClient() {
        long userId = 13L;
        int from = 0;
        int size = 9;
        ResponseEntity<Object> responseMock = ResponseEntity.ok().build();

        when(itemRequestClient.getOtherItemRequests(userId, from, size)).thenReturn(responseMock);

        ResponseEntity<Object> result = itemRequestGateway.getOthersItemRequests(userId, from, size);

        assertEquals(responseMock, result);
        verify(itemRequestClient).getOtherItemRequests(userId, from, size);
    }

    @Test
    void getItemRequestById_delegatesToClient() {
        Long id = 55L;
        ResponseEntity<Object> responseMock = ResponseEntity.ok().build();

        when(itemRequestClient.getItemRequestById(id)).thenReturn(responseMock);

        ResponseEntity<Object> result = itemRequestGateway.getItemRequestById(id);

        assertEquals(responseMock, result);
        verify(itemRequestClient).getItemRequestById(id);
    }
}