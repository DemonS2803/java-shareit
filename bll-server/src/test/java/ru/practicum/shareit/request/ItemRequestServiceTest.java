package ru.practicum.shareit.itemrequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.BllServerApp;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.testutil.DatabaseCleaner;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.common.testutil.TestStubs.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
       webEnvironment = SpringBootTest.WebEnvironment.MOCK,
       classes = BllServerApp.class)
@Transactional
public class ItemRequestServiceTest {

   @Autowired
   private ItemRequestService itemRequestService;
   @Autowired
   private UserService userService;
   @Autowired
   private DatabaseCleaner databaseCleaner;

   @BeforeEach
   void setUp() {
       databaseCleaner.cleanAllTables();
       userService.saveUser(VALID_CREATE_USER_DTO_1);
       userService.saveUser(VALID_CREATE_USER_DTO_2);
   }

   @Test
   void createItemRequest_validRequest_shouldCreateAndReturnRequest() {
       ItemRequestDto createdRequest = itemRequestService.createItemRequest(VALID_CREATE_ITEMREQUEST_DTO, VALID_USER_1.getId());
       assertNotNull(createdRequest);
       assertEquals("Need a new gaming laptop", createdRequest.getDescription());
   }

   @Test
   void createItemRequest_invalidUserId_shouldThrowException() {
       assertThrows(NotFoundException.class, () -> {
           itemRequestService.createItemRequest(VALID_CREATE_ITEMREQUEST_DTO, 999L);
       });
   }

   @Test
   void getAllOtherUsersItemRequests_validUserId_shouldReturnAllOtherUserRequests() {
       itemRequestService.createItemRequest(VALID_CREATE_ITEMREQUEST_DTO, VALID_USER_1.getId());
       ItemRequestDto request2 = itemRequestService.createItemRequest(new CreateItemRequestDto("Need a new mouse"), VALID_USER_2.getId());

       List<ItemRequestDto> requests = itemRequestService.getAllOtherUsersItemRequests(VALID_USER_1.getId());
       assertNotNull(requests);
       assertEquals(1, requests.size());
       assertTrue(requests.stream().anyMatch(r -> r.getDescription().contains("new mouse")));
   }

   @Test
   void getAllOtherUsersItemRequests_userWithNoRequests_shouldReturnEmptyList() {
       List<ItemRequestDto> requests = itemRequestService.getAllOtherUsersItemRequests(VALID_USER_1.getId());
       assertNotNull(requests);
       assertTrue(requests.isEmpty());
   }

   @Test
   void getUserItemRequests_validUserId_shouldReturnAllUserRequests() {
       ItemRequestDto request1 = itemRequestService.createItemRequest(VALID_CREATE_ITEMREQUEST_DTO, VALID_USER_1.getId());
       itemRequestService.createItemRequest(new CreateItemRequestDto("Need a new mouse"), VALID_USER_1.getId());

       List<ItemRequestDto> requests = itemRequestService.getUserItemRequests(VALID_USER_1.getId());
       assertNotNull(requests);
       assertEquals(2, requests.size());
   }

   @Test
   void getUserItemRequests_userWithNoRequests_shouldReturnEmptyList() {
       List<ItemRequestDto> requests = itemRequestService.getUserItemRequests(VALID_USER_1.getId());
       assertNotNull(requests);
       assertTrue(requests.isEmpty());
   }

   @Test
   void getItemRequestById_validRequestId_shouldReturnRequest() {
       ItemRequestDto createdRequest = itemRequestService.createItemRequest(VALID_CREATE_ITEMREQUEST_DTO, VALID_USER_1.getId());
       ItemRequestDto retrievedRequest = itemRequestService.getItemRequestById(createdRequest.getId());
       assertNotNull(retrievedRequest);
       assertEquals("Need a new gaming laptop", retrievedRequest.getDescription());
   }

   @Test
   void getItemRequestById_invalidRequestId_shouldThrowException() {
       assertThrows(NotFoundException.class, () -> {
           itemRequestService.getItemRequestById(999L);
       });
   }
}
