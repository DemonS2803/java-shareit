package ru.practicum.shareit.user;

import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.common.exception.DuplicateDataException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.common.testutil.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final Long NON_EXISTENT_ID = 999L;

    private static final CreateUserDto VALID_CREATE_USER_DTO_1 = CreateUserDto.builder()
            .email("john.doe@example.com")
            .name("John")
            .surname("Doe")
            .build();

    private static final UpdateUserDto VALID_UPDATE_USER_DTO_1 = UpdateUserDto.builder()
            .email("john.updated@example.com")
            .name("John Updated")
            .surname("Doe Updated")
            .build();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setup() {
        databaseCleaner.cleanAllTables();
    }

    @Test
    void createUser_validUser_shouldSaveAndReturnUser() throws Exception {
        String responseJson = mockMvc.perform(post(HttpConstants.USER_API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_CREATE_USER_DTO_1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(VALID_CREATE_USER_DTO_1.getEmail()))
                .andExpect(jsonPath("$.name").value(VALID_CREATE_USER_DTO_1.getName()))
                .andExpect(jsonPath("$.surname").value(VALID_CREATE_USER_DTO_1.getSurname()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto savedUser = objectMapper.readValue(responseJson, UserDto.class);

        assertNotNull(savedUser.getId());
        assertEquals(VALID_CREATE_USER_DTO_1.getEmail(), savedUser.getEmail());
        assertEquals(VALID_CREATE_USER_DTO_1.getName(), savedUser.getName());
        assertEquals(VALID_CREATE_USER_DTO_1.getSurname(), savedUser.getSurname());
    }

    @Test
    void createUser_duplicateEmail_shouldThrowException() throws Exception {
        userService.saveUser(VALID_CREATE_USER_DTO_1);

        CreateUserDto duplicateUserDto = CreateUserDto.builder()
                .email(VALID_CREATE_USER_DTO_1.getEmail())
                .name("JJJane")
                .surname("Smith")
                .build();

        mockMvc.perform(post(HttpConstants.USER_API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUserDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertInstanceOf(DuplicateDataException.class,
                        result.getResolvedException()));
    }

    @Test
    void createUser_nullSurname_shouldSaveSuccessfully() throws Exception {
        CreateUserDto dto = CreateUserDto.builder()
                .email("nosurname@example.com")
                .name("No")
                .surname(null)
                .build();

        String responseJson = mockMvc.perform(post(HttpConstants.USER_API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.name").value(dto.getName()))
                .andExpect(jsonPath("$.surname").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto savedUser = objectMapper.readValue(responseJson, UserDto.class);
        assertNotNull(savedUser);
        assertNull(savedUser.getSurname());
    }

    @Test
    void getUserById_validId_shouldReturnUser() throws Exception {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);
        Long userId = savedUser.getId();

        String responseJson = mockMvc.perform(get(HttpConstants.USER_API_PREFIX + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.name").value(savedUser.getName()))
                .andExpect(jsonPath("$.surname").value(savedUser.getSurname()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto retrievedUser = objectMapper.readValue(responseJson, UserDto.class);
        assertNotNull(retrievedUser);
        assertEquals(savedUser.getId(), retrievedUser.getId());
        assertEquals(savedUser.getEmail(), retrievedUser.getEmail());
        assertEquals(savedUser.getName(), retrievedUser.getName());
        assertEquals(savedUser.getSurname(), retrievedUser.getSurname());
    }

    @Test
    void getUserById_NON_EXISTENT_ID_shouldThrowException() throws Exception {
        mockMvc.perform(get(HttpConstants.USER_API_PREFIX + "/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                        result.getResolvedException()));
    }


    @Test
    void updateUser_validUpdate_shouldUpdateAndReturnUser() throws Exception {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        VALID_UPDATE_USER_DTO_1.setId(savedUser.getId());

        String responseJson = mockMvc.perform(patch(HttpConstants.USER_API_PREFIX + "/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_UPDATE_USER_DTO_1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.email").value(VALID_UPDATE_USER_DTO_1.getEmail()))
                .andExpect(jsonPath("$.name").value(VALID_UPDATE_USER_DTO_1.getName()))
                .andExpect(jsonPath("$.surname").value(VALID_UPDATE_USER_DTO_1.getSurname()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto updatedUser = objectMapper.readValue(responseJson, UserDto.class);
        assertNotNull(updatedUser);
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals(VALID_UPDATE_USER_DTO_1.getEmail(), updatedUser.getEmail());
        assertEquals(VALID_UPDATE_USER_DTO_1.getName(), updatedUser.getName());
        assertEquals(VALID_UPDATE_USER_DTO_1.getSurname(), updatedUser.getSurname());
    }

    @Test
    void updateUser_partialUpdate_shouldUpdateOnlyProvidedFields() throws Exception {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        UpdateUserDto partialUpdateDto = new UpdateUserDto();
        partialUpdateDto.setId(savedUser.getId());
        partialUpdateDto.setName("Updated Name Only");

        String responseJson = mockMvc.perform(patch(HttpConstants.USER_API_PREFIX + "/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("Updated Name Only"))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.surname").value(savedUser.getSurname()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto updatedUser = objectMapper.readValue(responseJson, UserDto.class);
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("Updated Name Only", updatedUser.getName());
        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
        assertEquals(savedUser.getSurname(), updatedUser.getSurname());
    }

    @Test
    void updateUser_updateEmailToExistingEmail_shouldThrowException() throws Exception {
        UserDto firstUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        CreateUserDto secondUserDto = CreateUserDto.builder()
                .email("jane.smith@example.com")
                .name("Jane")
                .surname("Smith")
                .build();
        UserDto secondUser = userService.saveUser(secondUserDto);

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(secondUser.getId());
        updateDto.setEmail(firstUser.getEmail());

        mockMvc.perform(patch(HttpConstants.USER_API_PREFIX + "/{id}", secondUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(result -> assertInstanceOf(DuplicateDataException.class,
                        result.getResolvedException()));
    }

    @Test
    void updateUser_updateEmailToSameEmail_shouldSucceed() throws Exception {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(savedUser.getId());
        updateDto.setEmail(savedUser.getEmail());

        mockMvc.perform(patch(HttpConstants.USER_API_PREFIX + "/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()));
    }

    @Test
    void updateUser_NON_EXISTENT_ID_shouldThrowException() throws Exception {
        VALID_UPDATE_USER_DTO_1.setId(NON_EXISTENT_ID);

        mockMvc.perform(patch(HttpConstants.USER_API_PREFIX + "/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_UPDATE_USER_DTO_1)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                        result.getResolvedException()));
    }

    @Test
    void updateUser_blankFields_shouldNotUpdateBlankFields() throws Exception {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(savedUser.getId());
        updateDto.setName("");
        updateDto.setEmail("");
        updateDto.setSurname("");

        String responseJson = mockMvc.perform(patch(HttpConstants.USER_API_PREFIX + "/{id}", savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto updatedUser = objectMapper.readValue(responseJson, UserDto.class);
        assertEquals(savedUser.getName(), updatedUser.getName());
        assertEquals(savedUser.getEmail(), updatedUser.getEmail());
        assertEquals(savedUser.getSurname(), updatedUser.getSurname());
    }

    @Test
    void deleteUser_validId_shouldDeleteUser() throws Exception {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);
        Long userId = savedUser.getId();

        String responseJson = mockMvc.perform(delete(HttpConstants.USER_API_PREFIX + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto deletedUser = objectMapper.readValue(responseJson, UserDto.class);
        assertNotNull(deletedUser);
        assertEquals(savedUser.getId(), deletedUser.getId());
        assertEquals(savedUser.getEmail(), deletedUser.getEmail());

        // Verify user no longer exists
        mockMvc.perform(get(HttpConstants.USER_API_PREFIX + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_NON_EXISTENT_ID_shouldThrowException() throws Exception {
        mockMvc.perform(delete(HttpConstants.USER_API_PREFIX + "/{id}", NON_EXISTENT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                        result.getResolvedException()));
    }

    @Test
    void deleteUser_twice_shouldThrowExceptionOnSecondDelete() throws Exception {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        mockMvc.perform(delete(HttpConstants.USER_API_PREFIX + "/{id}", savedUser.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(delete(HttpConstants.USER_API_PREFIX + "/{id}", savedUser.getId()))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(NotFoundException.class,
                        result.getResolvedException()));
    }

    @Test
    void getUsers_whenUsersExist_shouldReturnAllUsers() throws Exception {
        UserDto user1 = userService.saveUser(VALID_CREATE_USER_DTO_1);

        CreateUserDto user2Dto = CreateUserDto.builder()
                .email("mesuper@mail.ru")
                .name("Super")
                .surname("User")
                .build();
        UserDto user2 = userService.saveUser(user2Dto);

        CreateUserDto user3Dto = CreateUserDto.builder()
                .email("methird@mail.ru")
                .name("Third")
                .surname("User")
                .build();
        UserDto user3 = userService.saveUser(user3Dto);

        String responseJson = mockMvc.perform(get(HttpConstants.USER_API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(3)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> users = objectMapper.readValue(responseJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));

        assertNotNull(users);
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user1.getId())));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user2.getId())));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(user3.getId())));
    }

    @Test
    void getUsers_whenNoUsersExist_shouldReturnEmptyList() throws Exception {
        String responseJson = mockMvc.perform(get(HttpConstants.USER_API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> users = objectMapper.readValue(responseJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void getUsers_afterDeletion_shouldNotReturnDeletedUser() throws Exception {
        UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);

        userService.deleteUser(savedUser.getId());

        String responseJson = mockMvc.perform(get(HttpConstants.USER_API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> users = objectMapper.readValue(responseJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));

        assertFalse(users.stream().anyMatch(u -> u.getId().equals(savedUser.getId())));
    }

    @Test
    void fullUserLifecycle_shouldWorkCorrectly() throws Exception {
        String updatedName = "Updated name";

        // Create
        String createResponse = mockMvc.perform(post(HttpConstants.USER_API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_CREATE_USER_DTO_1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto createdUser = objectMapper.readValue(createResponse, UserDto.class);
        assertNotNull(createdUser.getId());

        // Update
        UpdateUserDto updateDto = UpdateUserDto.builder()
                .name(updatedName)
                .email(VALID_UPDATE_USER_DTO_1.getEmail())
                .surname(VALID_UPDATE_USER_DTO_1.getSurname())
                .build();
        updateDto.setId(createdUser.getId());

        String updateResponse = mockMvc.perform(patch(HttpConstants.USER_API_PREFIX + "/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto updatedUser = objectMapper.readValue(updateResponse, UserDto.class);
        assertEquals(updatedName, updatedUser.getName());

        // Get by ID
        String getResponse = mockMvc.perform(get(HttpConstants.USER_API_PREFIX + "/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto retrievedUser = objectMapper.readValue(getResponse, UserDto.class);
        assertEquals(updatedName, retrievedUser.getName());

        // Get all
        String getAllResponse = mockMvc.perform(get(HttpConstants.USER_API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> users = objectMapper.readValue(getAllResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, UserDto.class));
        assertTrue(users.stream().anyMatch(u -> u.getId().equals(createdUser.getId())));

        // Delete
        mockMvc.perform(delete(HttpConstants.USER_API_PREFIX + "/{id}", createdUser.getId()))
                .andExpect(status().isOk());

        // Verify deleted
        mockMvc.perform(get(HttpConstants.USER_API_PREFIX + "/{id}", createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
