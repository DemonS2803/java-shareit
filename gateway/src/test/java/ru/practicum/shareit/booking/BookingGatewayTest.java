package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingGatewayTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingGateway bookingGateway;

    @Test
    void getBookings_shouldDelegateToClient() {
        long userId = 100;
        String stateParam = "ALL";
        int from = 0, size = 10;
        BookingRequestState state = BookingRequestState.ALL;
        ResponseEntity<Object> fakeResp = ResponseEntity.ok().build();

        when(bookingClient.getBookings(userId, state, from, size)).thenReturn(fakeResp);

        ResponseEntity<Object> result = bookingGateway.getBookings(userId, stateParam, from, size);

        assertEquals(fakeResp, result);
        verify(bookingClient).getBookings(userId, state, from, size);
    }

    @Test
    void getOwnerBookings_shouldDelegateToClient() {
        long userId = 7;
        String stateParam = "ALL";
        int from = 5, size = 5;
        BookingRequestState state = BookingRequestState.ALL;
        ResponseEntity<Object> fakeResp = ResponseEntity.ok().build();

        when(bookingClient.getOwnerBooking(userId, state, from, size)).thenReturn(fakeResp);

        ResponseEntity<Object> result = bookingGateway.getOwnerBookings(userId, stateParam, from, size);

        assertEquals(fakeResp, result);
        verify(bookingClient).getOwnerBooking(userId, state, from, size);
    }

    @Test
    void bookItem_shouldDelegateToClient() {
        long userId = 1;
        CreateBookingDto dto = mock(CreateBookingDto.class);
        ResponseEntity<Object> fakeResp = ResponseEntity.ok().build();

        when(bookingClient.bookItem(userId, dto)).thenReturn(fakeResp);

        ResponseEntity<Object> result = bookingGateway.bookItem(userId, dto);

        assertEquals(fakeResp, result);
        verify(bookingClient).bookItem(userId, dto);
    }

    @Test
    void getBooking_shouldDelegateToClient() {
        long userId = 55;
        Long bookingId = 99L;
        ResponseEntity<Object> fakeResp = ResponseEntity.ok().build();

        when(bookingClient.getBooking(userId, bookingId)).thenReturn(fakeResp);

        ResponseEntity<Object> result = bookingGateway.getBooking(userId, bookingId);

        assertEquals(fakeResp, result);
        verify(bookingClient).getBooking(userId, bookingId);
    }

    @Test
    void approveBooking_shouldDelegateToClient() {
        long userId = 5;
        Long bookingId = 3L;
        boolean approve = true;
        ResponseEntity<Object> fakeResp = ResponseEntity.ok().build();

        when(bookingClient.approveBooking(userId, bookingId, approve)).thenReturn(fakeResp);

        ResponseEntity<Object> result = bookingGateway.approveBooking(userId, approve, bookingId);

        assertEquals(fakeResp, result);
        verify(bookingClient).approveBooking(userId, bookingId, approve);
    }
}