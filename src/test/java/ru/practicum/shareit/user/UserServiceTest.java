package ru.practicum.shareit.user;

import java.util.List;

import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.common.exception.DuplicateDataException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.testutil.DatabaseCleaner;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static ru.practicum.shareit.common.testutil.TestStubs.*;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = ShareItApp.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setup() {
        databaseCleaner.cleanAllTables();
    }

    @Test
    public void saveUser_validUser_shouldSaveAndReturnUser() {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(VALID_CREATE_USER_DTO_1.getEmail(), savedUser.getEmail());
        assertEquals(VALID_CREATE_USER_DTO_1.getName(), savedUser.getName());
        assertEquals(VALID_CREATE_USER_DTO_1.getSurname(), savedUser.getSurname());
    }

    @Test
    public void saveUser_duplicateEmail_shouldThrowException() {
        userService.saveUser(VALID_CREATE_USER_DTO_1);
        CreateUserDto duplicateUserDto = VALID_CREATE_USER_DTO_1.withName("JJJane");

        assertThrows(DuplicateDataException.class, () -> {
            userService.saveUser(duplicateUserDto);
        });
    }

    @Test
    public void saveUser_nullSurname_shouldSaveSuccessfully() {
        CreateUserDto dto = VALID_CREATE_USER_DTO_1.withSurname(null);

        UserDto savedUser = userService.saveUser(dto);
        assertNotNull(savedUser);
        assertNull(savedUser.getSurname());
    }

    @Test
    public void getUserById_validId_shouldReturnUser() {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);
        Long userId = savedUser.getId();

        UserDto retrievedUser = userService.getUserById(userId);

        assertNotNull(retrievedUser);
        assertEquals(savedUser.getId(), retrievedUser.getId());
        assertEquals(savedUser.getEmail(), retrievedUser.getEmail());
        assertEquals(savedUser.getName(), retrievedUser.getName());
        assertEquals(savedUser.getSurname(), retrievedUser.getSurname());
    }

    @Test
    public void getUserById_NON_EXISTENT_ID_shouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(NON_EXISTENT_ID);
        });
    }

    @Test
    public void getUserById_nullId_shouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(null);
        });
    }

    @Test
    public void updateUser_validUpdate_shouldUpdateAndReturnUser() {
        // First save a user
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        // Prepare update DTO
        VALID_UPDATE_USER_DTO_1.setId(savedUser.getId());

        // Update user
        UserDto updatedUser = userService.updateUser(VALID_UPDATE_USER_DTO_1);

        assertNotNull(updatedUser);
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals(VALID_UPDATE_USER_DTO_1.getEmail(), updatedUser.getEmail());
        assertEquals(VALID_UPDATE_USER_DTO_1.getName(), updatedUser.getName());
        assertEquals(VALID_UPDATE_USER_DTO_1.getSurname(), updatedUser.getSurname());
    }

    @Test
    public void updateUser_partialUpdate_shouldUpdateOnlyProvidedFields() {
        // First save a user
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        // Update only name
        UpdateUserDto partialUpdateDto = new UpdateUserDto();
        partialUpdateDto.setId(savedUser.getId());
        partialUpdateDto.setName("Updated Name Only");

        UserDto updatedUser = userService.updateUser(partialUpdateDto);

        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Updated Name Only", updatedUser.getName());
        // Email and surname should remain unchanged
        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
        assertEquals(savedUser.getSurname(), updatedUser.getSurname());
    }

    @Test
    public void updateUser_updateEmailToExistingEmail_shouldThrowException() {
        // Save first user
        UserDto firstUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        // Save second user
        CreateUserDto secondUserDto = new CreateUserDto();
        secondUserDto.setEmail("jane.smith@example.com");
        secondUserDto.setName("Jane");
        secondUserDto.setSurname("Smith");
        UserDto secondUser = userService.saveUser(secondUserDto);

        // Try to update second user's email to first user's email
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(secondUser.getId());
        updateDto.setEmail(firstUser.getEmail());

        assertThrows(DuplicateDataException.class, () -> {
            userService.updateUser(updateDto);
        });
    }

    @Test
    public void updateUser_updateEmailToSameEmail_shouldSucceed() {
        // Save a user
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        // Update with same email
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(savedUser.getId());
        updateDto.setEmail(savedUser.getEmail());

        UserDto updatedUser = userService.updateUser(updateDto);

        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void updateUser_NON_EXISTENT_ID_shouldThrowException() {
        VALID_UPDATE_USER_DTO_1.setId(999L);

        assertThrows(NotFoundException.class, () -> {
            userService.updateUser(VALID_UPDATE_USER_DTO_1);
        });
    }

    @Test
    public void updateUser_blankFields_shouldNotUpdateBlankFields() {
        // First save a user
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        // Try to update with blank name and email
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(savedUser.getId());
        updateDto.setName("");
        updateDto.setEmail("");
        updateDto.setSurname("");

        UserDto updatedUser = userService.updateUser(updateDto);

        // Fields should remain unchanged because blank values are ignored
        assertEquals(savedUser.getName(), updatedUser.getName());
        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
        assertEquals(savedUser.getSurname(), updatedUser.getSurname());
    }

    // ==================== DELETE USER TESTS ====================

    @Test
    public void deleteUser_validId_shouldDeleteUser() {
        // First save a user
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);
        Long userId = savedUser.getId();

        // Delete user
        UserDto deletedUser = userService.deleteUser(userId);

        assertNotNull(deletedUser);
        assertEquals(savedUser.getId(), deletedUser.getId());
        assertEquals(savedUser.getEmail(), deletedUser.getEmail());

        // Verify user no longer exists
        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(userId);
        });
    }

    @Test
    public void deleteUser_NON_EXISTENT_ID_shouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(NON_EXISTENT_ID);
        });
    }

    @Test
    public void deleteUser_nullId_shouldThrowException() {
        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(null);
        });
    }

    @Test
    public void deleteUser_twice_shouldThrowExceptionOnSecondDelete() {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);
        userService.deleteUser(savedUser.getId());

        assertThrows(NotFoundException.class, () -> {
            userService.deleteUser(savedUser.getId());
        });
    }

    @Test
    public void getUsers_whenUsersExist_shouldReturnAllUsers() {
        UserDto user1 = userService.saveUser(VALID_CREATE_USER_DTO_1);
        CreateUserDto user2Dto = VALID_CREATE_USER_DTO_1.withEmail("mesuper@mail.ru");
        UserDto user2 = userService.saveUser(user2Dto);
        CreateUserDto user3Dto = VALID_CREATE_USER_DTO_1.withEmail("methird@mail.ru");
        UserDto user3 = userService.saveUser(user3Dto);

        List<UserDto> users = userService.getUsers();
        assertNotNull(users);
        assertTrue(users.size() >= 3);
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user2.getId())));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user3.getId())));
    }

    @Test
    public void getUsers_whenNoUsersExist_shouldReturnEmptyList() {
        List<UserDto> users = userService.getUsers();

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    public void getUsers_afterDeletion_shouldNotReturnDeletedUser() {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        userService.deleteUser(savedUser.getId());

        List<UserDto> users = userService.getUsers();

        assertFalse(users.stream().anyMatch(u -> u.getId().equals(savedUser.getId())));
    }

    @Test
    public void fullUserLifecycle_shouldWorkCorrectly() {
        String updatedName = "Updated name";
        UserDto createdUser = userService.saveUser(VALID_CREATE_USER_DTO_1);
        assertNotNull(createdUser.getId());

        UpdateUserDto updateDto = VALID_UPDATE_USER_DTO_1.withName(updatedName);
        updateDto.setId(createdUser.getId());
        UserDto updatedUser = userService.updateUser(updateDto);
        assertEquals(updatedName, updatedUser.getName());

        UserDto retrievedUser = userService.getUserById(createdUser.getId());
        assertEquals(updatedName, retrievedUser.getName());

        List<UserDto> users = userService.getUsers();
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(createdUser.getId())));

        userService.deleteUser(createdUser.getId());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(createdUser.getId());
        });
    }
}
