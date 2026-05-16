package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.web.util.HttpConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(HttpConstants.BOOKING_API_PREFIX)
class BookingGateway {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(
            @RequestHeader(HttpConstants.SHARER_USER_HEADER) long userId,
            @RequestParam(name = HttpConstants.BOOKING_STATE_PARAM, defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(name = HttpConstants.PAGINATION_FROM_PARAM, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = HttpConstants.PAGINATION_SIZE_PARAM, defaultValue = "10") Integer size) {
        BookingRequestState state = BookingRequestState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(HttpConstants.SHARER_USER_HEADER) long userId,
            @RequestParam(name = HttpConstants.BOOKING_STATE_PARAM, defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(name = HttpConstants.PAGINATION_FROM_PARAM, defaultValue = "0") Integer from,
            @Positive @RequestParam(name = HttpConstants.PAGINATION_SIZE_PARAM, defaultValue = "10") Integer size) {
        BookingRequestState state = BookingRequestState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get user booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getOwnerBooking(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(HttpConstants.SHARER_USER_HEADER) long userId,
                                           @RequestBody @Valid CreateBookingDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HttpConstants.SHARER_USER_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(HttpConstants.SHARER_USER_HEADER) long userId,
                                                 @RequestParam(value = HttpConstants.APPROVE_BOOKING_PARAM) Boolean approve,
                                                 @PathVariable Long bookingId) {
        log.info("Approve booking {} = {}", bookingId, approve);
        return bookingClient.approveBooking(userId, bookingId, approve);
    }
}
