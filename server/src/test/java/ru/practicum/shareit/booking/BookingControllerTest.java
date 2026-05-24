package ru.practicum.shareit.booking;

import java.io.UnsupportedEncodingException;

import ru.practicum.shareit.BllServerApp;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.testutil.DatabaseCleaner;
import ru.practicum.shareit.common.web.util.HttpConstants;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static ru.practicum.shareit.common.testutil.TestStubs.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = BllServerApp.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

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
        UserDto savedUser2 = userService.saveUser(VALID_CREATE_USER_DTO_2);
        ItemDto savedItem = itemService.createItem(VALID_CREATE_ITEM_DTO, savedUser.getId());
    }

    @Test
    public void createBooking_validBooking_success200() throws Exception {
        addBooking(VALID_USER_ID_1, VALID_CREATE_BOOKING_DTO)
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void createBooking_missingEndDate_badRequest400() throws Exception {
        addBooking(VALID_USER_ID_2, VALID_CREATE_BOOKING_DTO.withEnd(null))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createBooking_unavailableItem_badRequest400() throws Exception {
        ItemDto unavailableItem = itemService.createItem(VALID_CREATE_ITEM_DTO.withAvailable(false), VALID_USER_ID_1);
        CreateBookingDto createBookingDto = VALID_CREATE_BOOKING_DTO.withItemId(unavailableItem.getId());
        addBooking(VALID_USER_ID_2, createBookingDto)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void approveBooking_approveBooking_success200() throws Exception {
        BookingDto created = getBookingFromRequest(
                addBooking(VALID_USER_ID_2, VALID_CREATE_BOOKING_DTO)
                        .andExpect(status().is2xxSuccessful())
        );
        approveBooking(created.getId(), VALID_USER_ID_1, true)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.status", is("APPROVED")));
    }


    @Test
    public void approveBooking_wrongOwner_forbidden403() throws Exception {
        BookingDto created = getBookingFromRequest(
                addBooking(VALID_USER_ID_2, VALID_CREATE_BOOKING_DTO)
                    .andExpect(status().is2xxSuccessful())
        );
        approveBooking(created.getId(), VALID_USER_ID_2, true)
                .andExpect(status().isForbidden());
    }

    @Test
    public void approveBooking_rejectBooking_success200() throws Exception {
        BookingDto created = getBookingFromRequest(
                addBooking(VALID_USER_ID_2, VALID_CREATE_BOOKING_DTO)
                    .andExpect(status().is2xxSuccessful())
        );
        approveBooking(created.getId(), VALID_USER_ID_1, false)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }

    @Test
    public void getBooking_validUser_success200() throws Exception {
        BookingDto created = getBookingFromRequest(
                addBooking(VALID_USER_ID_2, VALID_CREATE_BOOKING_DTO)
                    .andExpect(status().is2xxSuccessful())
        );

        BookingDto dto = getBookingFromRequest(
                getBooking(created.getId(), VALID_USER_ID_2)
                    .andExpect(status().is2xxSuccessful())
        );

        assertEquals(VALID_USER_ID_2, dto.getBooker().getId());
        assertEquals(VALID_ITEM_ID_1, dto.getItem().getId());
        assertEquals(true, dto.getItem().getAvailable());
        assertEquals(BookingState.WAITING, dto.getStatus());
    }

    @Test
    public void getBooking_notOwnerOrBookerUser_success200() throws Exception {
        UserDto randomGuy = userService.saveUser(VALID_CREATE_USER_DTO_1.withName("RandomGuy").withEmail("randomMail"));
        BookingDto created = getBookingFromRequest(
                addBooking(VALID_USER_ID_2, VALID_CREATE_BOOKING_DTO)
                    .andExpect(status().is2xxSuccessful())
        );
        getBooking(created.getId(), randomGuy.getId())
                .andExpect(status().isForbidden());

    }

    private ResultActions addBooking(Long userId, CreateBookingDto dto) throws Exception {
        return mvc.perform(post("/bookings")
                .header(HttpConstants.SHARER_USER_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON).content(
                        objectMapper.writeValueAsString(dto)
                ));
    }

    private ResultActions approveBooking(Long bookingId, Long userId, Boolean approve) throws Exception {
        return mvc.perform(patch("/bookings/" + bookingId)
                        .param(HttpConstants.APPROVE_BOOKING_PARAM, approve.toString())
                        .header(HttpConstants.SHARER_USER_HEADER, userId));
    }

    private ResultActions getBooking(Long bookingId, Long userId) throws Exception {
        return mvc.perform(get("/bookings/" + bookingId)
                .header(HttpConstants.SHARER_USER_HEADER, userId)
                .contentType(MediaType.APPLICATION_JSON).content(
                        objectMapper.writeValueAsString(VALID_CREATE_BOOKING_DTO)
                ));
    }

    private BookingDto getBookingFromRequest(ResultActions result) {
        try {
            return objectMapper.readValue(
                    result.andReturn().getResponse().getContentAsString(),
                    BookingDto.class
            );
        } catch (UnsupportedEncodingException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}