package ru.practicum.shareit.item;

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
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.hasSize;
import static ru.practicum.shareit.common.testutil.TestStubs.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@SpringBootTest(
       webEnvironment = SpringBootTest.WebEnvironment.MOCK,
       classes = BllServerApp.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

   @Autowired
   private MockMvc mvc;
   @Autowired
   private ObjectMapper objectMapper;
   @Autowired
   private UserService userService;
   @Autowired
   private ItemService itemService;
   @Autowired
   private DatabaseCleaner databaseCleaner;

   @BeforeEach
   void setup() {
       databaseCleaner.cleanAllTables();
       UserDto savedUser = userService.saveUser(VALID_CREATE_USER_DTO_1);
       ItemDto savedItem = itemService.createItem(VALID_CREATE_ITEM_DTO, savedUser.getId());
   }

   @Test
   public void createItem_validItem_success200() throws Exception {
       mvc.perform(post(HttpConstants.ITEM_API_PREFIX)
               .header(HttpConstants.SHARER_USER_HEADER, VALID_USER_ID_1)
               .contentType(MediaType.APPLICATION_JSON).content(
                       objectMapper.writeValueAsString(VALID_CREATE_ITEM_DTO)
               ))
               .andExpect(status().is2xxSuccessful());
   }

   @Test
   public void getItem_validItemId_success200() throws Exception {
       ItemDto savedItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_ID_1);
       mvc.perform(get(HttpConstants.ITEM_API_PREFIX + "/{itemId}", savedItem.getId())
               .header(HttpConstants.SHARER_USER_HEADER, VALID_USER_ID_1)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is2xxSuccessful());
   }

   @Test
   public void getUserItems_validUserId_success200() throws Exception {
       mvc.perform(get(HttpConstants.ITEM_API_PREFIX)
               .header(HttpConstants.SHARER_USER_HEADER, VALID_USER_ID_1)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$", hasSize(1)));
   }

   @Test
   public void searchItemByText_validText_success200() throws Exception {
       CreateItemDto createDto2 = new CreateItemDto("test", "tttt", true);
       ItemDto savedItem = itemService.createItem(createDto2, VALID_USER_ID_1);
       mvc.perform(get(HttpConstants.ITEM_API_PREFIX + "/search?text=" + savedItem.getName())
               .header(HttpConstants.SHARER_USER_HEADER, VALID_USER_ID_1)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().is2xxSuccessful())
               .andExpect(jsonPath("$", hasSize(1)));
   }
}
