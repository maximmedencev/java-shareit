package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Transactional
    @Modifying
    @Query("update Booking set status=?2 where id=?1")
    void updateBookingStatus(Long id, BookingStatus bookingStatus);

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId,
                                                              LocalDateTime start,
                                                              LocalDateTime end,
                                                              Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE item_id IN(SELECT id FROM items " +
            "WHERE owner_id = :ownerId) " +
            "ORDER BY b.end_date", nativeQuery = true)
    List<Booking> findAllOwnerItemsBookings(Long ownerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE item_id IN(SELECT id FROM items " +
            "WHERE owner_id = :ownerId) " +
            "AND b.end_date < NOW()::TIMESTAMP " +
            "ORDER BY b.end_date", nativeQuery = true)
    List<Booking> findAllOwnerItemsBookingsInPast(Long ownerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE item_id IN(SELECT id FROM items " +
            "WHERE owner_id = :ownerId) " +
            "AND b.start_date > NOW()::TIMESTAMP " +
            "ORDER BY b.end_date", nativeQuery = true)
    List<Booking> findAllOwnerItemsBookingsInFuture(Long ownerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE item_id IN(SELECT id FROM items " +
            "WHERE owner_id = :ownerId) AND b.start_date <= NOW()::TIMESTAMP " +
            "AND b.end_date >= NOW()::TIMESTAMP " +
            "ORDER BY b.end_date", nativeQuery = true)
    List<Booking> findAllCurrentOwnerItemsBooking(Long ownerId);

    @Query(value = "SELECT b.* FROM bookings b " +
            "WHERE item_id IN(SELECT id FROM items " +
            "WHERE owner_id = :ownerId)" +
            "AND status = :bookingStatus " +
            "ORDER BY b.end_date", nativeQuery = true)
    List<Booking> findAllOwnerItemsBookingWithStatus(Long ownerId, String bookingStatus);

    @Query(value = "SELECT MAX(b.end_date) FROM bookings b " +
            "WHERE b.item_id = :itemId " +
            "AND b.end_date < NOW()::TIMESTAMP", nativeQuery = true)
    LocalDateTime findLastBookingEndDate(Long itemId);

    @Query(value = "SELECT MIN(b.start_date) FROM bookings b " +
            "WHERE b.item_id = :itemId " +
            "AND b.start_date > NOW()::TIMESTAMP", nativeQuery = true)
    LocalDateTime findNextBookingStartDate(Long itemId);

    @Query(value = "SELECT EXISTS(SELECT FROM bookings WHERE booker_id = ?2 " +
            "AND item_id = ?1 " +
            "AND status = 'APPROVED' " +
            "AND end_date < NOW()::TIMESTAMP)", nativeQuery = true)
    Boolean isUserBookedTheItem(Long itemId, Long userId);


}
