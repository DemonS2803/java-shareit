package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {

    private UUID id;
    private UUID itemId;
    private boolean isApproved;
    private LocalDateTime from;
    private LocalDateTime to;
    private String feedback;

}
