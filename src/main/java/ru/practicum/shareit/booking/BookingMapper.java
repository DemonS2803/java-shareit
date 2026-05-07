package ru.practicum.shareit.booking;

import java.util.List;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

public class BookingMapper {

    public static BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getFromTime());
        dto.setEnd(booking.getToTime());
        dto.setItem(ItemMapper.toDto(booking.getItem()));
        dto.setBooker(UserMapper.toDto(booking.getUser()));
        dto.setStatus(booking.getState());
        return dto;
    }

    public static Booking fromDto(CreateBookingDto dto) {
        Booking booking = new Booking();
        booking.setFromTime(dto.getStart());
        booking.setToTime(dto.getEnd());
        return booking;
    }

    public static List<BookingDto> toDto(List<Booking> bookingList) {
        return bookingList.stream()
                .map(BookingMapper::toDto)
                .toList();
    }

}
