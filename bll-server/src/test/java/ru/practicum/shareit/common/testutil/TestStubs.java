package ru.practicum.shareit.common.testutil;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.time.LocalDateTime;

public class TestStubs {

    // Common stubs
    public static final LocalDateTime VALID_BEFORE_1_MONTH_LDT = LocalDateTime.now().minusMonths(1);
    public static final LocalDateTime VALID_BEFORE_1_DAY_LDT = LocalDateTime.now().minusDays(1);
    public static final LocalDateTime VALID_BEFORE_1_HOUR_LDT = LocalDateTime.now().minusHours(1);
    public static final LocalDateTime VALID_AFTER_1_MONTH_LDT = LocalDateTime.now().plusMonths(1);
    public static final LocalDateTime VALID_AFTER_1_DAY_LDT = LocalDateTime.now().plusDays(1);
    public static final LocalDateTime VALID_AFTER_1_HOUR_LDT = LocalDateTime.now().plusHours(1);
    public static final String VERY_LONG_STRING = "A".repeat(1000);
    public static final Long NON_EXISTENT_ID = Long.MAX_VALUE;

    // User stubs
    public static final Long VALID_USER_ID_1 = 1L;
    public static final String VALID_EMAIL_1 = "john.doe@example.com";
    public static final String VALID_NAME_1 = "John";
    public static final String VALID_SURNAME_1 = "Doe";
    public static final String INVALID_USER_EMAIL = "valid.email!@yandex.ru";
    public static final CreateUserDto VALID_CREATE_USER_DTO_1 = new CreateUserDto(
            VALID_EMAIL_1, VALID_NAME_1, VALID_SURNAME_1
    );
    public static final UpdateUserDto VALID_UPDATE_USER_DTO_1 = new UpdateUserDto(
            VALID_USER_ID_1, VALID_EMAIL_1, VALID_NAME_1, VALID_SURNAME_1
    );
    public static final User VALID_USER_1 = new User(
            VALID_USER_ID_1, VALID_NAME_1, VALID_SURNAME_1, VALID_EMAIL_1
    );
    public static final Long VALID_USER_ID_2 = 2L;
    public static final String VALID_EMAIL_2 = "jane@example.com";
    public static final String VALID_NAME_2 = "jane";
    public static final String VALID_SURNAME_2 = "super";
    public static final User VALID_USER_2 = new User(
            VALID_USER_ID_2, VALID_NAME_2, VALID_SURNAME_2, VALID_EMAIL_2
    );
    public static final CreateUserDto VALID_CREATE_USER_DTO_2 = new CreateUserDto(
            VALID_EMAIL_2, VALID_NAME_2, VALID_SURNAME_2
    );


    // Item stubs
    public static final Long VALID_ITEM_ID_1 = 1L;
    public static final String VALID_ITEM_NAME_1 = "item1";
    public static final String VALID_ITEM_DESCRIPTION_1 = "description1";
    public static final boolean ITEM_AVAILABLE = true;
    public static final CreateItemDto VALID_CREATE_ITEM_DTO = new CreateItemDto(
            VALID_ITEM_NAME_1, VALID_ITEM_DESCRIPTION_1, ITEM_AVAILABLE
    );
    public static final UpdateItemDto VALID_UPDATE_ITEM_DTO = new UpdateItemDto(
            VALID_ITEM_ID_1, VALID_ITEM_NAME_1, VALID_ITEM_DESCRIPTION_1, ITEM_AVAILABLE
    );
    public static final Item VALID_ITEM_1 = new Item(
            VALID_ITEM_ID_1,
            VALID_ITEM_NAME_1,
            VALID_ITEM_DESCRIPTION_1,
            VALID_USER_1,
            ITEM_AVAILABLE
    );

    // Booking stubs
    public static final CreateBookingDto VALID_CREATE_BOOKING_DTO = new CreateBookingDto(
            VALID_ITEM_ID_1, VALID_AFTER_1_HOUR_LDT, VALID_AFTER_1_DAY_LDT
    );

}
