package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.dto.NearestBookingsDto;
import ru.practicum.shareit.common.exception.ActionNotPermittedForUserException;
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
    public List<BookingDto> getBookingsByBooker(Long bookerId, BookingRequestState state) {
        userService.getUserById(bookerId);
        log.debug("Get bookings bo booker {}", bookerId);
        List<Booking> filteredBookings = switch (state) {
            case ALL -> bookingRepository.findBookingsByBookerId(bookerId);
            case PAST -> bookingRepository.findPastBookingsByBookerId(bookerId);
            case CURRENT -> bookingRepository.findCurrentBookingsByBookerId(bookerId);
            case FUTURE -> bookingRepository.findFutureBookingsByBookerId(bookerId);
            case WAITING -> bookingRepository.findBookingsByBookerIdAndState(bookerId, BookingState.WAITING);
            case REJECTED -> bookingRepository.findBookingsByBookerIdAndState(bookerId, BookingState.REJECTED);
        };
        log.debug("Found {} bookings for user {} with request state {}", filteredBookings.size(), bookerId, state);
        return BookingMapper.toDto(filteredBookings);
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long ownerId, BookingRequestState state) {
        userService.getUserById(ownerId);
        log.debug("Get bookings by owner {}", ownerId);
        List<Booking> filteredBookings = switch (state) {
            case ALL -> bookingRepository.findBookingsByOwnerId(ownerId);
            case PAST -> bookingRepository.findPastBookingsByOwnerId(ownerId);
            case CURRENT -> bookingRepository.findCurrentBookingsByOwnerId(ownerId);
            case FUTURE -> bookingRepository.findFutureBookingsByOwnerId(ownerId);
            case WAITING -> bookingRepository.findBookingsByOwnerIdAndState(ownerId, BookingState.WAITING);
            case REJECTED -> bookingRepository.findBookingsByOwnerIdAndState(ownerId, BookingState.REJECTED);
        };
        log.debug("Found {} bookings for user {} with request state {}", filteredBookings.size(), ownerId, state);
        return BookingMapper.toDto(filteredBookings);
    }

    @Override
    public Map<Long, NearestBookingsDto> getNearestBookingsForItems(List<Long> itemsId) {
        log.info("Get nearest bookings for items {}", itemsId);
        List<Booking> itemBookings = bookingRepository.findBookingsByItemIdIn(itemsId);
        Map<Long, NearestBookingsDto> resultMap = new HashMap<>();
        itemsId.forEach(id -> resultMap.put(id, new NearestBookingsDto()));

        for (Booking booking : itemBookings) {
            Long itemId = booking.getItem().getId();
            LocalDateTime now = LocalDateTime.now();
            if (!resultMap.containsKey(itemId)) {
                log.error("Item {} not included in repo query. Damn", itemId);
                resultMap.putIfAbsent(itemId, new NearestBookingsDto());
            }

            // case for previous
            if (now.isAfter(booking.getToTime())) {
                Optional<BookingDto> prev = resultMap.get(itemId).getPrevious();
                // set value if null or older
                if (prev.isEmpty() || prev.get().getEnd().isAfter(booking.getToTime())) {
                    resultMap.get(itemId).setPrevious(BookingMapper.toDto(booking));
                }
            }
            // case for next
            if (now.isBefore(booking.getFromTime())) {
                Optional<BookingDto> next = resultMap.get(itemId).getNext();
                // set value if null or earlier
                if (next.isEmpty() || next.get().getStart().isBefore(booking.getFromTime())) {
                    resultMap.get(itemId).setNext(BookingMapper.toDto(booking));
                }
            }
        }
        return resultMap;
    }

}
