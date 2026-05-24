package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findBookingsById(Long id);

    List<Booking> findBookingsByItemIdIn(Collection<Long> itemsId);

    /**
     * @param bookerId - id of user, which created booking
     * @return list of bookings, created by user
     */
    @Query(value = """
        select book from Booking book
         where  book.user.id = :bookerId
    """)
    List<Booking> findBookingsByBookerId(Long bookerId, Pageable page);

    @Query(value = """
        select book from Booking book
         where  book.user.id = :bookerId
    """)
    List<Booking> findBookingsByBookerId(Long bookerId);

    @Query(value = """
        select book from Booking book
         where book.toTime <= :now
            and book.user.id = :bookerId
    """)
    List<Booking> findPastBookingsByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Pageable page);

    @Query(value = """
        select book from Booking book
         where book.toTime <= :now
            and book.user.id = :bookerId
    """)
    List<Booking> findPastBookingsByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now);


    @Query(value = """
        select book from Booking book
         where book.toTime > :now
             and book.fromTime < :now
             and book.user.id = :bookerId
    """)
    List<Booking> findCurrentBookingsByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Pageable page);

    @Query(value = """
        select book from Booking book
        where book.fromTime > :now
             and book.user.id = :bookerId
    """)
    List<Booking> findFutureBookingsByBookerId(@Param("bookerId") Long bookerId, @Param("now") LocalDateTime now, Pageable page);

    @Query(value = """
        select book from Booking book
        where book.user.id = :bookerId
            and book.state = :state
    """)
    List<Booking> findBookingsByBookerIdAndState(
            @Param("bookerId") Long bookerId,
            @Param("state") BookingState state,
            Pageable page
    );


    /**
     * @return list of bookings for all items owned by specific user
     */
    @Query(value = """
        select book from Booking book
            join Item it on book.item.id = it.id
            where it.owner.id = :ownerId
    """)
    List<Booking> findBookingsByOwnerId(@Param("ownerId") Long ownerId, Pageable page);

    @Query(value = """
        select book from Booking book
            join Item it on book.item.id = it.id
            where it.owner.id = :ownerId
                and book.toTime < :now
    """)
    List<Booking> findPastBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable page);

    @Query(value = """
        select book from Booking book
         join Item it on book.item.id = it.id
            where it.owner.id = :ownerId
                and book.toTime > :now
                and book.fromTime < :now
    """)
    List<Booking> findCurrentBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable page);

    @Query(value = """
        select book from Booking book
        join Item it on book.item.id = it.id
            where it.owner.id = :ownerId
                and book.fromTime > :now
    """)
    List<Booking> findFutureBookingsByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable page);

    @Query(value = """
        select book from Booking book
        join Item it on book.item.id = it.id
            where it.owner.id = :ownerId
                and book.state = :state
    """)
    List<Booking> findBookingsByOwnerIdAndState(
            @Param("ownerId") Long ownerId,
            @Param("state") BookingState state,
            Pageable page
    );

}
