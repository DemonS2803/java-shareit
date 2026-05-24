package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.BllServerApp;
import ru.practicum.shareit.common.testutil.DatabaseCleaner;
import ru.practicum.shareit.common.testutil.TestStubs;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(
       webEnvironment = SpringBootTest.WebEnvironment.MOCK,
       classes = BllServerApp.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {

   @Autowired
   private MockMvc mvc;
   @Autowired
   private ObjectMapper objectMapper;
   @Autowired
   private UserService userService;
   @Autowired
   private ItemRequestService itemRequestService;
   @Autowired
   private DatabaseCleaner databaseCleaner;

   private UserDto savedUser;

   @BeforeEach
   void setup() {
       databaseCleaner.cleanAllTables();
       savedUser = userService.saveUser(TestStubs.VALID_CREATE_USER_DTO_1);
   }

   @Test
   public void createItemRequest_validRequest_success200() throws Exception {
       mvc.perform(post(HttpConstants.ITEM_REQUEST_API_PREFIX)
               .header(HttpConstants.SHARER_USER_HEADER, TestStubs.VALID_USER_ID_1)
               .contentType(MediaType.APPLICATION_JSON).content(
                       objectMapper.writeValueAsString(TestStubs.VALID_CREATE_ITEMREQUEST_DTO)
               ))
               .andExpect(status().is2xxSuccessful());
   }

   @Test
   public void getUserItemRequests_validUserId_success200() throws Exception {
       ItemRequestDto savedItemRequest = itemRequestService.createItemRequest(TestStubs.VALID_CREATE_ITEMREQUEST_DTO, savedUser.getId());
       mvc.perform(get(HttpConstants.ITEM_REQUEST_API_PREFIX)
               .header(HttpConstants.SHARER_USER_HEADER, TestStubs.VALID_USER_ID_1)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$", hasSize(1)));
   }

   @Test
   public void getAllOtherUsersItemRequests_validUserId_success200() throws Exception {
       ItemRequestDto savedItemRequest = itemRequestService.createItemRequest(TestStubs.VALID_CREATE_ITEMREQUEST_DTO, savedUser.getId());
       mvc.perform(get(HttpConstants.ITEM_REQUEST_API_PREFIX + "/all")
               .header(HttpConstants.SHARER_USER_HEADER, TestStubs.VALID_USER_ID_1)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$", hasSize(0)));
   }

   @Test
   public void getItemRequestById_validRequestId_success200() throws Exception {
       ItemRequestDto savedItemRequest = itemRequestService.createItemRequest(TestStubs.VALID_CREATE_ITEMREQUEST_DTO, TestStubs.VALID_USER_ID_1);
       mvc.perform(get(HttpConstants.ITEM_REQUEST_API_PREFIX + "/{id}", savedItemRequest.getId())
               .header(HttpConstants.SHARER_USER_HEADER, TestStubs.VALID_USER_ID_1)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is2xxSuccessful());
   }
}
