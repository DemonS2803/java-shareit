package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Map;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.NearestBookingsDto;

public interface BookingService {

    BookingDto createBooking(CreateBookingDto createBookingDto, Long userId);

    BookingDto approveBooking(boolean approve, Long bookingId, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    /**
     *
     * @param itemsId - list of item ids: [1, 2, ...., 99]
     * @return map with
     * itemId -> nearestBooking(prev, next)
     */
    Map<Long, NearestBookingsDto> getNearestBookingsForItems(List<Long> itemsId);

    List<BookingDto> getBookingsByBooker(Long bookerId, BookingRequestState state);

    List<BookingDto> getBookingsByOwner(Long ownerId, BookingRequestState state);

}
