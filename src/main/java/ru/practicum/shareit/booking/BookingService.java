package ru.practicum.shareit.booking;

import java.util.List;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

public interface BookingService {

    BookingDto createBooking(CreateBookingDto createBookingDto, Long userId);

    BookingDto approveBooking(boolean approve, Long bookingId, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByBooker(Long bookerId, BookingRequestState state);

    List<BookingDto> getBookingsByOwner(Long ownerId, BookingRequestState state);

}
