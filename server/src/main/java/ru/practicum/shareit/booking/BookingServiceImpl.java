package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.common.exception.ActionNotPermittedForUserException;
import ru.practicum.shareit.common.exception.BookingApproveFailedException;
import ru.practicum.shareit.common.exception.BookingUnavailableException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(CreateBookingDto dto, Long userId) {
        User user = userService.getUserEntityById(userId);
        Item item = itemService.getItemEntityById(dto.getItemId());
        if (!item.isAvailable()) {
            throw new BookingUnavailableException(
                    "Couldn't create booking for item. Item " + item.getId() + " is unavailable"
            );
        }

        log.info("Create booking of {} by {}", item, user);
        Booking booking = BookingMapper.fromDto(dto);
        booking.setItem(item);
        booking.setUser(user);
        booking.setState(BookingState.WAITING);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveBooking(boolean approve, Long bookingId, Long userId) {
        log.debug("User {} want to approve booking {}", userId, bookingId);
        Booking dbBooking = getBookingByIdOrThrow(bookingId);
        if (!Objects.equals(dbBooking.getItem().getOwner().getId(), userId)) {
            log.error("User {} can't approve booking {}", userId, bookingId);
            throw new ActionNotPermittedForUserException("User can't approve booking for item, he is not owned");
        }
        if (!BookingState.WAITING.equals(dbBooking.getState())) {
            throw new BookingApproveFailedException("User can't approve booking with status " + dbBooking.getState());
        }
        userService.getUserEntityById(userId);


        BookingState newState = approve ? BookingState.APPROVED : BookingState.REJECTED;
        log.info("Set status {} for booking {}", newState, bookingId);
        dbBooking.setState(newState);
        return BookingMapper.toDto(bookingRepository.save(dbBooking));
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        User user = userService.getUserEntityById(userId);
        Booking booking = getBookingByIdOrThrow(bookingId);
        if (!Objects.equals(user.getId(), booking.getUser().getId()) &&
            !Objects.equals(user.getId(), booking.getItem().getOwner().getId())) {
            throw new ActionNotPermittedForUserException(
                    "Only booker and item owner could request full booking info"
            );
        }
        log.debug("Get booking {}", bookingId);
        return BookingMapper.toDto(booking);
    }

    private Booking getBookingByIdOrThrow(Long id) {
        return bookingRepository.findBookingsById(id)
                .orElseThrow(() -> new NotFoundException("Booking with id " + id + " not found"));
    }

    @Override
    public List<BookingDto> getBookingsByBooker(Long bookerId, BookingRequestState state, int from, int size) {
        userService.getUserById(bookerId);
        log.debug("Get bookings bo booker {}", bookerId);
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Booking> filteredBookings = switch (state) {
            case ALL -> bookingRepository.findBookingsByBookerId(bookerId, page);
            case PAST -> bookingRepository.findPastBookingsByBookerId(bookerId, LocalDateTime.now(), page);
            case CURRENT -> bookingRepository.findCurrentBookingsByBookerId(bookerId, LocalDateTime.now(), page);
            case FUTURE -> bookingRepository.findFutureBookingsByBookerId(bookerId, LocalDateTime.now(), page);
            case WAITING -> bookingRepository.findBookingsByBookerIdAndState(bookerId, BookingState.WAITING, page);
            case REJECTED -> bookingRepository.findBookingsByBookerIdAndState(bookerId, BookingState.REJECTED, page);
        };
        log.debug("Found {} bookings for user {} with request state {}", filteredBookings.size(), bookerId, state);
        return BookingMapper.toDto(filteredBookings);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId, BookingRequestState state, int from, int size) {
        userService.getUserById(ownerId);
        log.debug("Get bookings by owner {}", ownerId);
        PageRequest page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<Booking> filteredBookings = switch (state) {
            case ALL -> bookingRepository.findBookingsByOwnerId(ownerId, page);
            case PAST -> bookingRepository.findPastBookingsByOwnerId(ownerId, LocalDateTime.now(), page);
            case CURRENT -> bookingRepository.findCurrentBookingsByOwnerId(ownerId, LocalDateTime.now(), page);
            case FUTURE -> bookingRepository.findFutureBookingsByOwnerId(ownerId, LocalDateTime.now(), page);
            case WAITING -> bookingRepository.findBookingsByOwnerIdAndState(ownerId, BookingState.WAITING, page);
            case REJECTED -> bookingRepository.findBookingsByOwnerIdAndState(ownerId, BookingState.REJECTED, page);
        };
        log.debug("Found {} bookings for user {} with request state {}", filteredBookings.size(), ownerId, state);
        return BookingMapper.toDto(filteredBookings);
    }

}
