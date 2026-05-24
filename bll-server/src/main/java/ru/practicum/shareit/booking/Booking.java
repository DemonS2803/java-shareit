package ru.practicum.shareit.booking;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@Entity
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Item item;
    @ManyToOne
    private User user;
    @Enumerated(EnumType.ORDINAL)
    private BookingState state;
    private boolean approved;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;



}
