package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Map;

import ru.practicum.shareit.booking.dto.NearestBookingsDto;

public interface BookingItemInfoService {

    /**
     *
     * @param itemsId - list of item ids: [1, 2, ...., 99]
     * @return map with
     * itemId -> nearestBooking(prev, next)
     */
    Map<Long, NearestBookingsDto> getNearestBookingsForItems(List<Long> itemsId);

    /**
     * Checks user past bookings for selected item id
     */
    boolean isUserHadPastBookingForItem(Long userId, Long itemId);

}
