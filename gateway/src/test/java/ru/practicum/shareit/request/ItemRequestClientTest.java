package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Spy
    ItemRequestClient itemRequestClient = new ItemRequestClient(
            "http://localhost", new RestTemplateBuilder()
    );

    @Test
    void getItemRequestById_callsGetWithCorrectPath() {
        Long itemId = 123L;
        ResponseEntity<Object> responseMock = ResponseEntity.ok().build();

        doReturn(responseMock).when(itemRequestClient).get("/" + itemId);

        ResponseEntity<Object> result = itemRequestClient.getItemRequestById(itemId);

        assertEquals(responseMock, result);
        verify(itemRequestClient).get("/" + itemId);
    }

    @Test
    void getUserItemRequests_callsGetWithCorrectParams() {
        long userId = 5L;
        int from = 1;
        int size = 20;
        ResponseEntity<Object> responseMock = ResponseEntity.ok().build();

        Map<String, Object> params = Map.of(
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        String getPath = itemRequestClient.buildParametersFromMap(params);

        doReturn(responseMock).when(itemRequestClient).get(getPath, userId);

        ResponseEntity<Object> result = itemRequestClient.getUserItemRequests(userId, from, size);

        assertEquals(responseMock, result);
        verify(itemRequestClient).get(getPath, userId);
    }

    @Test
    void getOtherItemRequests_callsGetWithCorrectParams() {
        long userId = 7L;
        int from = 0;
        int size = 5;
        ResponseEntity<Object> responseMock = ResponseEntity.ok().build();

        Map<String, Object> params = Map.of(
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        String getPath = "/all" + itemRequestClient.buildParametersFromMap(params);

        doReturn(responseMock).when(itemRequestClient).get(getPath, userId);

        ResponseEntity<Object> result = itemRequestClient.getOtherItemRequests(userId, from, size);

        assertEquals(responseMock, result);
        verify(itemRequestClient).get(getPath, userId);
    }

    @Test
    void createItemRequest_callsPostWithCorrectArgs() {
        long userId = 9L;
        CreateItemRequestDto dto = mock(CreateItemRequestDto.class);
        ResponseEntity<Object> responseMock = ResponseEntity.ok().build();

        doReturn(responseMock).when(itemRequestClient).post("", userId, dto);

        ResponseEntity<Object> result = itemRequestClient.createItemRequest(userId, dto);

        assertEquals(responseMock, result);
        verify(itemRequestClient).post("", userId, dto);
    }
}
