package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    // Используем spy, чтобы мокать базовые методы get/post/patch
    @Spy
    private ItemClient itemClient = new ItemClient(
            "http://server",
            new RestTemplateBuilder()
    );

    @Test
    void getItem_delegatesToBaseGet() {
        long userId = 1L;
        Long itemId = 2L;
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        doReturn(resp).when(itemClient).get("/" + itemId, userId);

        ResponseEntity<Object> result = itemClient.getItem(userId, itemId);

        assertEquals(resp, result);
        verify(itemClient).get("/" + itemId, userId);
    }

    @Test
    void getItems_delegatesToBaseGetWithCorrectParams() {
        long userId = 2L;
        int from = 0, size = 10;
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        Map<String, Object> params = Map.of(
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        String path = itemClient.buildParametersFromMap(params);

        doReturn(resp).when(itemClient).get(path, userId);

        ResponseEntity<Object> result = itemClient.getItems(userId, from, size);

        assertEquals(resp, result);
        verify(itemClient).get(path, userId);
    }

    @Test
    void searchItemsByText_delegatesToBaseGetWithCorrectParams() {
        long userId = 5L;
        String text = "phone";
        int from = 1, size = 5;
        Map<String, Object> params = Map.of(
                HttpConstants.SEARCH_ITEM_TEXT_PARAM, text,
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        String path = itemClient.buildParametersFromMap(params);

        ResponseEntity<Object> resp = ResponseEntity.ok().build();
        doReturn(resp).when(itemClient).get("/search" + path, userId, params);

        ResponseEntity<Object> result = itemClient.searchItemsByText(userId, text, from, size);

        assertEquals(resp, result);
        verify(itemClient).get("/search" + path, userId, params);
    }

    @Test
    void createItem_delegatesToBasePost() {
        long userId = 4L;
        CreateItemDto dto = mock(CreateItemDto.class);
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        doReturn(resp).when(itemClient).post("", userId, dto);

        ResponseEntity<Object> result = itemClient.createItem(userId, dto);

        assertEquals(resp, result);
        verify(itemClient).post("", userId, dto);
    }

    @Test
    void updateItem_delegatesToBasePatch() {
        long userId = 3L;
        Long itemId = 8L;
        UpdateItemDto dto = mock(UpdateItemDto.class);
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        doReturn(resp).when(itemClient).patch("/" + itemId, userId, dto);

        ResponseEntity<Object> result = itemClient.updateItem(userId, itemId, dto);

        assertEquals(resp, result);
        verify(itemClient).patch("/" + itemId, userId, dto);
    }

    @Test
    void commentItem_delegatesToBasePost() {
        long userId = 2L;
        Long itemId = 9L;
        CreateCommentDto dto = mock(CreateCommentDto.class);
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        doReturn(resp).when(itemClient).post("/" + itemId + "/comment", userId, dto);

        ResponseEntity<Object> result = itemClient.commentItem(userId, itemId, dto);

        assertEquals(resp, result);
        verify(itemClient).post("/" + itemId + "/comment", userId, dto);
    }
}
