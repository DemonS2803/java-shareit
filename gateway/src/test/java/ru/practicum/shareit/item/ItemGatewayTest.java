package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ItemGatewayTest {

    @Mock
    private ItemClient itemClient;

    @InjectMocks
    private ItemGateway itemGateway;

    @Test
    void createItem_shouldDelegateToClient() {
        long userId = 1L;
        CreateItemDto dto = mock(CreateItemDto.class);
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        when(itemClient.createItem(userId, dto)).thenReturn(resp);

        ResponseEntity<Object> result = itemGateway.createItem(dto, userId);

        assertEquals(resp, result);
        verify(itemClient).createItem(userId, dto);
    }

    @Test
    void updateItem_shouldDelegateToClientAndSetId() {
        Long itemId = 123L;
        long userId = 15L;
        UpdateItemDto dto = mock(UpdateItemDto.class);

        ResponseEntity<Object> resp = ResponseEntity.ok().build();
        when(itemClient.updateItem(userId, itemId, dto)).thenReturn(resp);

        ResponseEntity<Object> result = itemGateway.updateItem(itemId, dto, userId);

        assertEquals(resp, result);
        verify(dto).setId(itemId);
        verify(itemClient).updateItem(userId, itemId, dto);
    }

    @Test
    void getItem_shouldDelegateToClient() {
        Long itemId = 55L;
        long userId = 2L;
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        when(itemClient.getItem(userId, itemId)).thenReturn(resp);

        ResponseEntity<Object> result = itemGateway.getItem(itemId, userId);

        assertEquals(resp, result);
        verify(itemClient).getItem(userId, itemId);
    }

    @Test
    void getUserItems_shouldDelegateToClient() {
        long userId = 5L;
        int from = 0, size = 10;
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        when(itemClient.getItems(userId, from, size)).thenReturn(resp);

        ResponseEntity<Object> result = itemGateway.getUserItems(userId, from, size);

        assertEquals(resp, result);
        verify(itemClient).getItems(userId, from, size);
    }

    @Test
    void searchItemByText_shouldDelegateToClient() {
        Long userId = 50L;
        String text = "test";
        int from = 0, size = 10;
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        when(itemClient.searchItemsByText(userId, text, from, size)).thenReturn(resp);

        ResponseEntity<Object> result = itemGateway.searchItemByText(text, userId, from, size);

        assertEquals(resp, result);
        verify(itemClient).searchItemsByText(userId, text, from, size);
    }

    @Test
    void commentItem_shouldDelegateToClient() {
        long userId = 1L;
        Long itemId = 77L;
        CreateCommentDto dto = mock(CreateCommentDto.class);
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        when(itemClient.commentItem(userId, itemId, dto)).thenReturn(resp);

        ResponseEntity<Object> result = itemGateway.commentItem(userId, itemId, dto);

        assertEquals(resp, result);
        verify(itemClient).commentItem(userId, itemId, dto);
    }
}
