package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @InjectMocks
    @Spy
    private UserClient userClient = new UserClient(
            "http://server", new org.springframework.boot.web.client.RestTemplateBuilder()
    );

    @Test
    void getUser_shouldCallBaseClientGetWithCorrectParameters() {
        long userId = 42L;
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().build();
        doReturn(mockResponse).when(userClient).get("/" + userId, userId);

        ResponseEntity<Object> result = userClient.getUser(userId);

        verify(userClient, times(1)).get("/" + userId, userId);
        assertEquals(mockResponse, result);
    }

    @Test
    void getUsers_shouldCallBaseClientGetWithCorrectParameters() {
        int from = 0, size = 10;
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().build();

        doReturn(mockResponse).when(userClient).get(anyString());

        ResponseEntity<Object> result = userClient.getUsers(from, size);

        verify(userClient, times(1)).get(anyString());
        assertEquals(mockResponse, result);
    }

    @Test
    void createUser_shouldCallBaseClientPostWithCorrectParameters() {
        CreateUserDto dto = mock(CreateUserDto.class);
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().build();
        doReturn(mockResponse).when(userClient).post("", dto);

        ResponseEntity<Object> result = userClient.createUser(dto);

        verify(userClient, times(1)).post("", dto);
        assertEquals(mockResponse, result);
    }

    @Test
    void updateUser_shouldCallBaseClientPatchWithCorrectParameters() {
        long userId = 33L;
        UpdateUserDto dto = mock(UpdateUserDto.class);
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().build();
        doReturn(mockResponse).when(userClient).patch("/" + userId, dto);

        ResponseEntity<Object> result = userClient.updateUser(userId, dto);

        verify(userClient, times(1)).patch("/" + userId, dto);
        assertEquals(mockResponse, result);
    }

    @Test
    void deleteUser_shouldCallBaseClientDeleteWithCorrectParameters() {
        long userId = 12L;
        ResponseEntity<Object> mockResponse = ResponseEntity.ok().build();
        doReturn(mockResponse).when(userClient).delete("/" + userId);

        ResponseEntity<Object> result = userClient.deleteUser(userId);

        verify(userClient, times(1)).delete("/" + userId);
        assertEquals(mockResponse, result);
    }
}
