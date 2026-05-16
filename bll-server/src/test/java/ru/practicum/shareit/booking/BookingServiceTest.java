package ru.practicum.shareit.booking;

import java.util.List;

import ru.practicum.shareit.BllServerApp;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.exception.BookingUnavailableException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.testutil.DatabaseCleaner;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.user.UserService;
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
        classes = BllServerApp.class)
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;
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
    void createBooking_createValidBooking_shouldSuccess() {
        BookingDto bookingDto = bookingService.createBooking(VALID_CREATE_BOOKING_DTO, VALID_USER_ID_1);
        assertEquals(VALID_CREATE_BOOKING_DTO.getStart(), bookingDto.getStart());
        assertEquals(VALID_CREATE_BOOKING_DTO.getEnd(), bookingDto.getEnd());
        assertEquals(BookingState.WAITING, bookingDto.getStatus());
    }

    @Test
    void createBooking_createBookingNonExistingItem_shouldFail() {
        CreateBookingDto createBookingDto = VALID_CREATE_BOOKING_DTO.withItemId(NON_EXISTENT_ID);
        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(createBookingDto, VALID_USER_ID_1);
        });
    }

    @Test
    void createBooking_createBookingUnavailableItem_shouldFail() {
        UpdateItemDto updateItemDto = VALID_UPDATE_ITEM_DTO.withAvailable(false);
        itemService.updateItem(updateItemDto, VALID_USER_ID_1);
        assertThrows(BookingUnavailableException.class, () -> {
            bookingService.createBooking(VALID_CREATE_BOOKING_DTO, VALID_USER_ID_1);
        });
    }

    @Test
    void approveBooking_ownerSuccessApproved_shouldSuccess() {
        BookingDto bookingDto = bookingService.createBooking(VALID_CREATE_BOOKING_DTO, VALID_USER_ID_1);
        BookingDto approvedBookingDto = bookingService.approveBooking(true, bookingDto.getId(), VALID_USER_ID_1);

        bookingDto = bookingService.getBookingById(bookingDto.getId(), VALID_USER_ID_1);
        assertEquals(BookingState.APPROVED, bookingDto.getStatus());
        assertEquals(VALID_USER_ID_1, bookingDto.getBooker().getId());
    }

    @Test
    void approveBooking_ownerNotApproved_shouldSuccess() {
        BookingDto bookingDto = bookingService.createBooking(VALID_CREATE_BOOKING_DTO, VALID_USER_ID_1);
        BookingDto approvedBookingDto = bookingService.approveBooking(false, bookingDto.getId(), VALID_USER_ID_1);

        bookingDto = bookingService.getBookingById(bookingDto.getId(), VALID_USER_ID_1);
        assertEquals(BookingState.REJECTED, bookingDto.getStatus());
        assertEquals(VALID_USER_ID_1, bookingDto.getBooker().getId());
    }

    @Test
    void getBooking_getBookingsByOwner_shouldSuccess() {
        userService.saveUser(VALID_CREATE_USER_DTO_2);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO, VALID_USER_ID_2);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO.withEnd(VALID_BEFORE_1_MONTH_LDT), VALID_USER_ID_1);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO.withEnd(VALID_BEFORE_1_MONTH_LDT), VALID_USER_ID_2);

        List<BookingDto> ownerBookings = bookingService.getBookingsByOwner(VALID_USER_ID_1, BookingRequestState.ALL);
        assertEquals(3, ownerBookings.size());
    }

    @Test
    void getBooking_getBookingsByBooker_shouldSuccess() {
        userService.saveUser(VALID_CREATE_USER_DTO_2);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO, VALID_USER_ID_1);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO.withEnd(VALID_BEFORE_1_MONTH_LDT), VALID_USER_ID_1);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO.withEnd(VALID_BEFORE_1_MONTH_LDT), VALID_USER_ID_2);

        List<BookingDto> ownerBookings = bookingService.getBookingsByBooker(VALID_USER_ID_1, BookingRequestState.ALL);
        assertEquals(2, ownerBookings.size());
    }

    @Test
    void getBooking_getBookingsByNonExistingBooker_shouldSuccess() {
        userService.saveUser(VALID_CREATE_USER_DTO_2);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO, VALID_USER_ID_1);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO.withEnd(VALID_BEFORE_1_MONTH_LDT), VALID_USER_ID_1);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO.withEnd(VALID_BEFORE_1_MONTH_LDT), VALID_USER_ID_2);

        assertThrows(NotFoundException.class, () -> {
            List<BookingDto> ownerBookings = bookingService.getBookingsByBooker(NON_EXISTENT_ID, BookingRequestState.ALL);
        });
    }

    @Test
    void getBooking_getAllBookings_shouldSuccess() {
        userService.saveUser(VALID_CREATE_USER_DTO_2);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO, VALID_USER_ID_1);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO.withEnd(VALID_BEFORE_1_MONTH_LDT), VALID_USER_ID_1);
        bookingService.createBooking(VALID_CREATE_BOOKING_DTO.withEnd(VALID_BEFORE_1_MONTH_LDT), VALID_USER_ID_2);

        List<BookingDto> ownerBookings = bookingService.getBookingsByBooker(VALID_USER_ID_1, BookingRequestState.ALL);
        assertEquals(2, ownerBookings.size());
    }
}
