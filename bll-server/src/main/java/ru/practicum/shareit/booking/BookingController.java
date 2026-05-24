package ru.practicum.shareit.booking;

import java.util.Collection;

import jakarta.validation.Valid;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.web.util.HttpConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = HttpConstants.BOOKING_API_PREFIX)
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto getBookingInfo(
            @PathVariable Long bookingId,
            @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.debug("User {} get info about booking {}", userId, bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping("")
    public Collection<BookingDto> getUserBookingsInfo(
            @RequestParam(value = HttpConstants.BOOKING_STATE_PARAM, defaultValue = "ALL") BookingRequestState state,
            @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = HttpConstants.PAGINATION_FROM_PARAM, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = HttpConstants.PAGINATION_SIZE_PARAM, defaultValue = "10") Integer size) {
        log.debug("User {} get {} bookings", userId, state);
        return bookingService.getBookingsByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getOwnerBookingsInfo(
            @RequestParam(value = HttpConstants.BOOKING_STATE_PARAM, defaultValue = "ALL") BookingRequestState state,
            @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId,
            @PositiveOrZero @RequestParam(name = HttpConstants.PAGINATION_FROM_PARAM, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = HttpConstants.PAGINATION_SIZE_PARAM, defaultValue = "10") Integer size) {
        log.debug("User {} get {} bookings", userId, state);
        return bookingService.getBookingsByOwner(userId, state, from, size);
    }

    @PostMapping("")
    public BookingDto createBooking(
            @RequestBody @Valid CreateBookingDto dto,
            @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId) {
        log.info("User {} create booking: {}", userId, dto);
        return bookingService.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @PathVariable Long bookingId,
            @RequestHeader(value = HttpConstants.SHARER_USER_HEADER) Long userId,
            @RequestParam(value = HttpConstants.APPROVE_BOOKING_PARAM) Boolean approved) {
        log.info("User {} wanna approve ({}) booking {}", userId, approved, bookingId);
        return bookingService.approveBooking(approved, bookingId, userId);
    }

}
