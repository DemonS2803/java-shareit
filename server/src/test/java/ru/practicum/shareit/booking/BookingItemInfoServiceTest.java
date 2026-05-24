package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.NearestBookingsDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.*;
import ru.practicum.shareit.common.testutil.TestStubs;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.UserService;

import java.util.*;

class BookingItemInfoServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private BookingItemInfoServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNearestBookingsForItems_returnsCorrectResults() {
        List<Long> itemIds = Arrays.asList(1L, 2L);
        Long itemId1 = 1L, itemId2 = 2L;

        // Создаем бронирования
        Booking pastBooking = createBooking(itemId1, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));
        Booking futureBooking = createBooking(itemId1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        Booking anotherFuture = createBooking(itemId2, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusDays(1));
        Booking anotherPast = createBooking(itemId2, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2));

        when(bookingRepository.findBookingsByItemIdIn(itemIds))
                .thenReturn(Arrays.asList(pastBooking, futureBooking, anotherFuture, anotherPast));

        Map<Long, NearestBookingsDto> result = service.getNearestBookingsForItems(itemIds);

        assertNotNull(result);
        assertTrue(result.containsKey(1L));
        assertTrue(result.containsKey(2L));

        NearestBookingsDto dto1 = result.get(1L);
        assertTrue(dto1.getPrevious().isPresent());
        assertEquals(futureBooking.getToTime(), BookingMapper.toDto(futureBooking).getEnd());

        NearestBookingsDto dto2 = result.get(2L);
        assertTrue(dto2.getNext().isPresent());
        assertEquals(anotherFuture.getFromTime(), BookingMapper.toDto(anotherFuture).getStart());
    }

    @Test
    void isUserHadPastBookingForItem_returnsTrueIfBookingExists() {
        Long userId = 100L;
        Long itemId = 50L;
        Booking pastBooking = createBooking(itemId, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(3));
        when(bookingRepository.findPastBookingsByBookerId(eq(userId), any()))
                .thenReturn(Collections.singletonList(pastBooking));

        boolean hasPastBooking = service.isUserHadPastBookingForItem(userId, itemId);
        assertTrue(hasPastBooking);
    }

    @Test
    void isUserHadPastBookingForItem_returnsFalseIfNoBooking() {
        Long userId = 101L;
        Long itemId = 51L;
        when(bookingRepository.findPastBookingsByBookerId(eq(userId), any()))
                .thenReturn(Collections.emptyList());

        boolean result = service.isUserHadPastBookingForItem(userId, itemId);
        assertFalse(result);
    }

    private Booking createBooking(Long itemId, LocalDateTime from, LocalDateTime to) {
        Booking booking = mock(Booking.class);
        Item item = mock(Item.class);
        when(item.getId()).thenReturn(itemId);
        when(item.getOwner()).thenReturn(TestStubs.VALID_USER_1);
        when(booking.getItem()).thenReturn(item);
        when(booking.getFromTime()).thenReturn(from);
        when(booking.getToTime()).thenReturn(to);
        when(booking.getUser()).thenReturn(TestStubs.VALID_USER_1);
        return booking;
    }
}