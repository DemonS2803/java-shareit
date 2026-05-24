package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.web.util.HttpConstants;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Spy
    private BookingClient bookingClient = new BookingClient(
            "http://server", new RestTemplateBuilder()
    );

    @Test
    void getBookings_delegatesToBaseGetWithCorrectArgs() {
        long userId = 11;
        BookingRequestState state = BookingRequestState.ALL;
        int from = 2, size = 4;

        ResponseEntity<Object> resp = ResponseEntity.ok().build();
        Map<String, Object> expectedParams = Map.of(
                HttpConstants.BOOKING_STATE_PARAM, state.name(),
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );

        doReturn(resp).when(bookingClient).get(anyString(), eq(userId), anyMap());

        ResponseEntity<Object> result = bookingClient.getBookings(userId, state, from, size);

        verify(bookingClient, times(1)).get(anyString(), eq(userId), eq(expectedParams));
        assertEquals(resp, result);
    }

    @Test
    void getOwnerBooking_delegatesToBaseGetWithCorrectArgs() {
        long userId = 20;
        BookingRequestState state = BookingRequestState.REJECTED;
        int from = 6, size = 10;
        ResponseEntity<Object> resp = ResponseEntity.ok().build();
        Map<String, Object> expectedParams = Map.of(
                HttpConstants.BOOKING_STATE_PARAM, state.name(),
                HttpConstants.PAGINATION_FROM_PARAM, from,
                HttpConstants.PAGINATION_SIZE_PARAM, size
        );
        doReturn(resp).when(bookingClient).get(anyString(), eq(userId), anyMap());

        ResponseEntity<Object> result = bookingClient.getOwnerBooking(userId, state, from, size);

        verify(bookingClient, times(1)).get(anyString(), eq(userId), eq(expectedParams));
        assertEquals(resp, result);
    }

    @Test
    void bookItem_delegatesToBasePostWithCorrectArgs() {
        long userId = 30;
        CreateBookingDto dto = mock(CreateBookingDto.class);
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        doReturn(resp).when(bookingClient).post("", userId, dto);

        ResponseEntity<Object> result = bookingClient.bookItem(userId, dto);

        verify(bookingClient, times(1)).post("", userId, dto);
        assertEquals(resp, result);
    }

    @Test
    void getBooking_delegatesToBaseGetWithCorrectArgs() {
        long userId = 40;
        Long bookingId = 7L;
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        doReturn(resp).when(bookingClient).get("/" + bookingId, userId);

        ResponseEntity<Object> result = bookingClient.getBooking(userId, bookingId);

        verify(bookingClient, times(1)).get("/" + bookingId, userId);
        assertEquals(resp, result);
    }

    @Test
    void approveBooking_delegatesToBasePatchWithCorrectArgs() {
        long userId = 50;
        Long bookingId = 8L;
        boolean approve = true;
        String paramName = HttpConstants.APPROVE_BOOKING_PARAM;
        String expectedPath = String.format("/%s?%s=%s", bookingId, paramName, approve);
        ResponseEntity<Object> resp = ResponseEntity.ok().build();

        doReturn(resp).when(bookingClient).patch(expectedPath, userId);

        ResponseEntity<Object> result = bookingClient.approveBooking(userId, bookingId, approve);

        verify(bookingClient, times(1)).patch(expectedPath, userId);
        assertEquals(resp, result);
    }
}
