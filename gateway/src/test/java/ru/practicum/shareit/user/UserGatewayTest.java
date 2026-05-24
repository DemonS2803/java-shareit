package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGatewayTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserGateway userGateway;

    @Test
    void getUsers_shouldDelegateToUserClient() {
        when(userClient.getUsers(0, 10)).thenReturn(ResponseEntity.ok().build());

        userGateway.getUsers(0, 10);

        verify(userClient, times(1)).getUsers(0, 10);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void getUser_shouldDelegateToUserClient() {
        long id = 123L;
        when(userClient.getUser(id)).thenReturn(ResponseEntity.ok().build());

        userGateway.getUser(id);

        verify(userClient, times(1)).getUser(id);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void createUser_shouldDelegateToUserClient() {
        CreateUserDto dto = mock(CreateUserDto.class);
        when(userClient.createUser(dto)).thenReturn(ResponseEntity.ok().build());

        userGateway.createUser(dto);

        verify(userClient, times(1)).createUser(dto);
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void updateUser_shouldDelegateToUserClient() {
        UpdateUserDto dto = mock(UpdateUserDto.class);
        long id = 42L;
        when(userClient.updateUser(eq(id), eq(dto))).thenReturn(ResponseEntity.ok().build());

        userGateway.updateUser(dto, id);

        verify(dto, times(1)).setId(id); // Optionally verify id set
        verify(userClient, times(1)).updateUser(eq(id), eq(dto));
        verifyNoMoreInteractions(userClient);
    }

    @Test
    void deleteUser_shouldDelegateToUserClient() {
        long id = 13L;
        when(userClient.deleteUser(id)).thenReturn(ResponseEntity.ok().build());

        userGateway.deleteUser(id);

        verify(userClient, times(1)).deleteUser(id);
        verifyNoMoreInteractions(userClient);
    }
}