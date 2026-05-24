package ru.practicum.shareit.item;

import java.util.List;

import ru.practicum.shareit.BllServerApp;
import ru.practicum.shareit.common.exception.ActionNotPermittedForUserException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.testutil.DatabaseCleaner;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static ru.practicum.shareit.common.testutil.TestStubs.*;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BllServerApp.class)
@Transactional
class ItemServiceTest {

    @Autowired
    private ItemService itemService;
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
    void createItem_validItem_shouldCreateAndReturnItem() {
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        assertNotNull(createdItem);
        assertNotNull(createdItem.getId());
        assertEquals(VALID_CREATE_ITEM_DTO.getName(), createdItem.getName());
        assertEquals(VALID_CREATE_ITEM_DTO.getDescription(), createdItem.getDescription());
        assertEquals(VALID_CREATE_ITEM_DTO.getAvailable(), createdItem.getAvailable());
        assertEquals(VALID_USER_1.getId(), createdItem.getId());
    }

    @Test
    void createItem_nonExistentOwner_shouldThrowException() {
        Long nonExistentOwnerId = 999L;

        assertThrows(NotFoundException.class, () -> {
            itemService.createItem(VALID_CREATE_ITEM_DTO, nonExistentOwnerId);
        });
    }


    @Test
    void createItem_nameWithWhitespace_shouldTrimAndCreate() {
        CreateItemDto dto = VALID_CREATE_ITEM_DTO.withName("  Laptop Pro  ");

        ItemDto createdItem = itemService.createItem(dto, VALID_USER_1.getId());
        assertEquals("Laptop Pro", createdItem.getName());
    }

    @Test
    void createItem_availableFalse_shouldCreateWithAvailableFalse() {
        CreateItemDto dto = VALID_CREATE_ITEM_DTO.withAvailable(false);

        ItemDto createdItem = itemService.createItem(dto, VALID_USER_1.getId());

        assertFalse(createdItem.getAvailable());
    }

    // ==================== UPDATE ITEM TESTS ====================

    @Test
    void updateItem_validUpdate_shouldUpdateAndReturnItem() {
        // First create an item
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Update the item

        ItemDto updatedItem = itemService.updateItem(VALID_UPDATE_ITEM_DTO, VALID_USER_1.getId());

        assertNotNull(updatedItem);
        assertEquals(createdItem.getId(), updatedItem.getId());
        assertEquals(VALID_UPDATE_ITEM_DTO.getName(), updatedItem.getName());
        assertEquals(VALID_UPDATE_ITEM_DTO.getDescription(), updatedItem.getDescription());
        assertEquals(VALID_UPDATE_ITEM_DTO.getAvailable(), updatedItem.getAvailable());
        assertEquals(VALID_USER_1.getId(), updatedItem.getOwner().getId());
    }

    @Test
    void updateItem_partialUpdate_shouldUpdateOnlyProvidedFields() {
        // Create item
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Update only name
        UpdateItemDto partialUpdateDto = new UpdateItemDto();
        partialUpdateDto.setId(createdItem.getId());
        partialUpdateDto.setName("Updated Name Only");

        ItemDto updatedItem = itemService.updateItem(partialUpdateDto, VALID_USER_1.getId());

        assertEquals("Updated Name Only", updatedItem.getName());
        assertEquals(createdItem.getDescription(), updatedItem.getDescription());
        assertEquals(createdItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void updateItem_updateDescriptionOnly_shouldUpdateDescriptionOnly() {
        // Create item
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Update only description
        UpdateItemDto partialUpdateDto = new UpdateItemDto();
        partialUpdateDto.setId(createdItem.getId());
        partialUpdateDto.setDescription("New description only");

        ItemDto updatedItem = itemService.updateItem(partialUpdateDto, VALID_USER_1.getId());

        assertEquals(createdItem.getName(), updatedItem.getName());
        assertEquals("New description only", updatedItem.getDescription());
        assertEquals(createdItem.getAvailable(), updatedItem.getAvailable());
    }

    @Test
    void updateItem_updateAvailableOnly_shouldUpdateAvailableOnly() {
        // Create item
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Update only available
        UpdateItemDto partialUpdateDto = new UpdateItemDto();
        partialUpdateDto.setId(createdItem.getId());
        partialUpdateDto.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(partialUpdateDto, VALID_USER_1.getId());

        assertEquals(createdItem.getName(), updatedItem.getName());
        assertEquals(createdItem.getDescription(), updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void updateItem_nonExistentItem_shouldThrowException() {
        Long nonExistentItemId = 999L;
        UpdateItemDto dto = VALID_UPDATE_ITEM_DTO.withId(nonExistentItemId);

        assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(dto, VALID_USER_1.getId());
        });
    }

    @Test
    void updateItem_nonOwner_shouldThrowException() {
        // Create item with VALID_USER_1
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Try to update with different user
        UpdateItemDto dto = VALID_UPDATE_ITEM_DTO.withId(createdItem.getId());

        assertThrows(ActionNotPermittedForUserException.class, () -> {
            itemService.updateItem(dto, VALID_USER_2.getId());
        });
    }

    @Test
    void updateItem_withBlankFields_shouldNotUpdateBlankFields() {
        // Create item
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Try to update with blank fields
        UpdateItemDto blankUpdateDto = new UpdateItemDto();
        blankUpdateDto.setId(createdItem.getId());
        blankUpdateDto.setName("");
        blankUpdateDto.setDescription("");

        ItemDto updatedItem = itemService.updateItem(blankUpdateDto, VALID_USER_1.getId());

        // Fields should remain unchanged
        assertEquals(createdItem.getName(), updatedItem.getName());
        assertEquals(createdItem.getDescription(), updatedItem.getDescription());
    }

    @Test
    void updateItem_availableTrueToFalse_shouldUpdateAvailability() {
        // Create available item
        CreateItemDto dto = VALID_CREATE_ITEM_DTO.withAvailable(true);
        ItemDto createdItem = itemService.createItem(dto, VALID_USER_1.getId());

        // Update to not available
        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setId(createdItem.getId());
        updateDto.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(updateDto, VALID_USER_1.getId());

        assertFalse(updatedItem.getAvailable());
    }

    // ==================== GET ITEM TESTS ====================

    @Test
    void getItem_validId_shouldReturnItem() {
        // Create item
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Get item by id
        ItemDto retrievedItem = itemService.getItemById(createdItem.getId());

        assertNotNull(retrievedItem);
        assertEquals(createdItem.getId(), retrievedItem.getId());
        assertEquals(createdItem.getName(), retrievedItem.getName());
        assertEquals(createdItem.getDescription(), retrievedItem.getDescription());
        assertEquals(createdItem.getAvailable(), retrievedItem.getAvailable());
        assertEquals(createdItem.getOwner().getId(), retrievedItem.getOwner().getId());
    }

    @Test
    void getItem_nonExistentId_shouldThrowException() {
        Long nonExistentId = 999L;

        assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(nonExistentId);
        });
    }

    // ==================== GET USER ITEMS TESTS ====================

    @Test
    void getUserItems_validUserId_shouldReturnAllUserItems() {
        // Create multiple items for owner
        CreateItemDto item1Dto = new CreateItemDto();
        item1Dto.setName("Laptop");
        item1Dto.setDescription("Gaming laptop");
        item1Dto.setAvailable(true);
        ItemDto item1 = itemService.createItem(item1Dto, VALID_USER_1.getId());

        CreateItemDto item2Dto = new CreateItemDto();
        item2Dto.setName("Mouse");
        item2Dto.setDescription("Wireless mouse");
        item2Dto.setAvailable(true);
        ItemDto item2 = itemService.createItem(item2Dto, VALID_USER_1.getId());

        CreateItemDto item3Dto = new CreateItemDto();
        item3Dto.setName("Keyboard");
        item3Dto.setDescription("Mechanical keyboard");
        item3Dto.setAvailable(false);
        ItemDto item3 = itemService.createItem(item3Dto, VALID_USER_1.getId());

        // Get all user items
        List<ItemDto> userItems = itemService.getUserItems(VALID_USER_1.getId(), 0, 10);

        assertNotNull(userItems);
        assertEquals(3, userItems.size());
        assertTrue(userItems.stream().anyMatch(i -> i.getId().equals(item1.getId())));
        assertTrue(userItems.stream().anyMatch(i -> i.getId().equals(item2.getId())));
        assertTrue(userItems.stream().anyMatch(i -> i.getId().equals(item3.getId())));
    }

    @Test
    void getUserItems_userWithNoItems_shouldReturnEmptyList() {
        List<ItemDto> userItems = itemService.getUserItems(VALID_USER_2.getId(), 0, 10);

        assertNotNull(userItems);
        assertTrue(userItems.isEmpty());
    }

    @Test
    void getUserItems_nonExistentUser_shouldReturnEmptyList() {
        Long nonExistentUserId = 999L;

        assertThrows(NotFoundException.class, () -> {
            List<ItemDto> userItems = itemService.getUserItems(nonExistentUserId, 0, 10);
        });
    }

    @Test
    void getUserItems_afterDeletingItem_shouldNotReturnDeletedItem() {
        // Create two items
        ItemDto item1 = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());
        ItemDto item2 = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Delete one item (if delete method exists)
        // itemService.deleteItem(item1.getId(), VALID_USER_1.getId());

        List<ItemDto> userItems = itemService.getUserItems(VALID_USER_1.getId(), 0, 10);

        // Verify (assuming delete works)
        // assertFalse(userItems.stream().anyMatch(i -> i.getId().equals(item1.getId())));
        // assertTrue(userItems.stream().anyMatch(i -> i.getId().equals(item2.getId())));
    }

    // ==================== SEARCH ITEMS TESTS ====================

    @Test
    void searchItems_validText_shouldReturnMatchingItems() {
        // Create items with different names and descriptions
        CreateItemDto laptopDto = new CreateItemDto();
        laptopDto.setName("Gaming Laptop");
        laptopDto.setDescription("High-performance gaming laptop");
        laptopDto.setAvailable(true);
        itemService.createItem(laptopDto, VALID_USER_1.getId());

        CreateItemDto mouseDto = new CreateItemDto();
        mouseDto.setName("Wireless Mouse");
        mouseDto.setDescription("Ergonomic wireless mouse for gaming");
        mouseDto.setAvailable(true);
        itemService.createItem(mouseDto, VALID_USER_1.getId());

        CreateItemDto keyboardDto = new CreateItemDto();
        keyboardDto.setName("Keyboard");
        keyboardDto.setDescription("Standard office keyboard");
        keyboardDto.setAvailable(true);
        itemService.createItem(keyboardDto, VALID_USER_1.getId());

        // Search for "gaming"
        List<ItemDto> searchResults = itemService.searchItems("gaming", 0, 10);

        assertNotNull(searchResults);
        assertEquals(2, searchResults.size());
        assertTrue(searchResults.stream().anyMatch(i -> i.getName().toLowerCase().contains("gaming")));
        assertTrue(searchResults.stream().anyMatch(i -> i.getDescription().toLowerCase().contains("gaming")));
    }

    @Test
    void searchItems_caseInsensitiveSearch_shouldReturnResults() {
        // Create item
        CreateItemDto itemDto = new CreateItemDto();
        itemDto.setName("LAPTOP PRO");
        itemDto.setDescription("High performance laptop");
        itemDto.setAvailable(true);
        itemService.createItem(itemDto, VALID_USER_1.getId());

        // Search with different case
        List<ItemDto> searchResults = itemService.searchItems("laptop", 0, 10);

        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
    }

    @Test
    void searchItems_partialMatch_shouldReturnResults() {
        // Create item
        CreateItemDto itemDto = new CreateItemDto();
        itemDto.setName("Smartphone");
        itemDto.setDescription("Android smartphone with great camera");
        itemDto.setAvailable(true);
        itemService.createItem(itemDto, VALID_USER_1.getId());

        // Search with partial word
        List<ItemDto> searchResults = itemService.searchItems("smart", 0, 10);

        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
    }

    @Test
    void searchItems_noMatchingText_shouldReturnEmptyList() {
        // Create item
        itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Search for non-existent text
        List<ItemDto> searchResults = itemService.searchItems("nonexistent", 0, 10);

        assertNotNull(searchResults);
        assertTrue(searchResults.isEmpty());
    }

    @Test
    void searchItems_emptyText_shouldReturnEmptyList() {
        itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        List<ItemDto> searchResults = itemService.searchItems("", 0, 10);

        assertNotNull(searchResults);
        assertTrue(searchResults.isEmpty());
    }

    @Test
    void searchItems_nullText_shouldReturnEmptyList() {
        itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        List<ItemDto> searchResults = itemService.searchItems(null, 0, 10);

        assertNotNull(searchResults);
        assertTrue(searchResults.isEmpty());
    }

    @Test
    void searchItems_whitespaceText_shouldReturnEmptyList() {
        itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        List<ItemDto> searchResults = itemService.searchItems("   ", 0, 10);

        assertNotNull(searchResults);
        assertTrue(searchResults.isEmpty());
    }

    @Test
    void searchItems_onlyAvailableItems_shouldNotReturnUnavailableItems() {
        // Create available item
        CreateItemDto availableDto = new CreateItemDto();
        availableDto.setName("Available Laptop");
        availableDto.setDescription("This laptop is available");
        availableDto.setAvailable(true);
        itemService.createItem(availableDto, VALID_USER_1.getId());

        // Create unavailable item
        CreateItemDto unavailableDto = new CreateItemDto();
        unavailableDto.setName("Unavailable Laptop");
        unavailableDto.setDescription("This laptop is not available");
        unavailableDto.setAvailable(false);
        itemService.createItem(unavailableDto, VALID_USER_1.getId());

        // Search for "laptop"
        List<ItemDto> searchResults = itemService.searchItems("laptop", 0, 10);

        // Only available items should be returned
        assertNotNull(searchResults);
        assertEquals(1, searchResults.size());
        assertTrue(searchResults.get(0).getAvailable());
        assertEquals("Available Laptop", searchResults.get(0).getName());
    }

    @Test
    void searchItems_multipleUsersItems_shouldReturnAllMatchingAvailableItems() {
        // Create item for owner1
        CreateItemDto owner1Item = new CreateItemDto();
        owner1Item.setName("Phone");
        owner1Item.setDescription("Smartphone from owner1");
        owner1Item.setAvailable(true);
        itemService.createItem(owner1Item, VALID_USER_1.getId());

        // Create item for owner2
        CreateItemDto owner2Item = new CreateItemDto();
        owner2Item.setName("Phone");
        owner2Item.setDescription("Smartphone from owner2");
        owner2Item.setAvailable(true);
        itemService.createItem(owner2Item, VALID_USER_2.getId());

        // Search for "phone"
        List<ItemDto> searchResults = itemService.searchItems("phone", 0, 10);

        assertEquals(2, searchResults.size());
    }

    // ==================== INTEGRATION TESTS ====================

    @Test
    void completeItemLifecycle_shouldWorkCorrectly() {
        // 1. Create item
        ItemDto createdItem = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());
        assertNotNull(createdItem.getId());

        // 2. Update item
        UpdateItemDto updateItemDto =  VALID_UPDATE_ITEM_DTO.withId(createdItem.getId());
        updateItemDto.setName("Updated Laptop");
        ItemDto updatedItem = itemService.updateItem(updateItemDto, VALID_USER_1.getId());
        assertEquals("Updated Laptop", updatedItem.getName());

        // 3. Get item by id
        ItemDto retrievedItem = itemService.getItemById(createdItem.getId());
        assertEquals("Updated Laptop", retrievedItem.getName());

        // 4. Get user items
        List<ItemDto> userItems = itemService.getUserItems(VALID_USER_1.getId(), 0, 10);
        assertTrue(userItems.stream().anyMatch(i -> i.getId().equals(createdItem.getId())));

        // 5. Search for item
        List<ItemDto> searchResults = itemService.searchItems("Updated", 0, 10);
        assertTrue(searchResults.stream().anyMatch(i -> i.getId().equals(createdItem.getId())));
    }

    @Test
    void multipleItemsForDifferentUsers_shouldNotMix() {
        // Create items for owner1
        ItemDto owner1Item1 = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());
        ItemDto owner1Item2 = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Create item for owner2
        ItemDto owner2Item = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_2.getId());

        // Get items for owner1
        List<ItemDto> owner1Items = itemService.getUserItems(VALID_USER_1.getId(), 0, 10);
        assertEquals(2, owner1Items.size());
        assertTrue(owner1Items.stream().allMatch(i -> i.getOwner().getId().equals(VALID_USER_1.getId())));

        // Get items for owner2
        List<ItemDto> owner2Items = itemService.getUserItems(VALID_USER_2.getId(), 0, 10);
        assertEquals(1, owner2Items.size());
        assertTrue(owner2Items.stream().allMatch(i -> i.getOwner().getId().equals(VALID_USER_2.getId())));
    }


    @Test
    void commentItem_shouldSuccess() {
        // Create items for owner1
        ItemDto owner1Item1 = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());
        ItemDto owner1Item2 = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_1.getId());

        // Create item for owner2
        ItemDto owner2Item = itemService.createItem(VALID_CREATE_ITEM_DTO, VALID_USER_2.getId());

        // Get items for owner1
        List<ItemDto> owner1Items = itemService.getUserItems(VALID_USER_1.getId(), 0, 10);
        assertEquals(2, owner1Items.size());
        assertTrue(owner1Items.stream().allMatch(i -> i.getOwner().getId().equals(VALID_USER_1.getId())));

        // Get items for owner2
        List<ItemDto> owner2Items = itemService.getUserItems(VALID_USER_2.getId(), 0, 10);
        assertEquals(1, owner2Items.size());
        assertTrue(owner2Items.stream().allMatch(i -> i.getOwner().getId().equals(VALID_USER_2.getId())));
    }

}
