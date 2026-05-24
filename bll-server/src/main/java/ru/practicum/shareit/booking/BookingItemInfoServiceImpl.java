package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NearestBookingsDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingItemInfoServiceImpl implements BookingItemInfoService {

    private final BookingRepository bookingRepository;

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

    @Override
    public boolean isUserHadPastBookingForItem(Long userId, Long itemId) {
        List<Booking> userBookings = bookingRepository.findBookingsByBookerId(userId);
        return userBookings.stream()
                .filter(booking -> booking.getToTime().isBefore(LocalDateTime.now()))
                .anyMatch(booking -> Objects.equals(booking.getItem().getId(), itemId));
    }

}
