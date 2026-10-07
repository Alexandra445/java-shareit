package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    boolean existsByItemIdAndBookerIdAndEndBeforeAndStatus(
            Long itemId,
            Long bookerId,
            LocalDateTime now,
            BookingState status
    );

    List<Booking> findByItemOwnerIdAndEndBeforeAndStatusOrderByEndDesc(
            Long ownerId,
            LocalDateTime now,
            BookingState status
    );

    List<Booking> findByItemOwnerIdAndStartAfterAndStatusOrderByStartAsc(
            Long ownerId,
            LocalDateTime now,
            BookingState status
    );

    List<Booking> findByBookerIdAndStartLessThanEqualAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findByBookerIdAndEndLessThanEqualOrderByStartDesc(
            Long bookerId,
            LocalDateTime end
    );

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime start
    );

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(
            Long bookerId,
            BookingState status
    );

    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndAfterOrderByStartDesc(
            Long ownerId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Booking> findByItemOwnerIdAndEndLessThanEqualOrderByStartDesc(
            Long ownerId,
            LocalDateTime end
    );

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long ownerId,
            LocalDateTime start
    );

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(
            Long ownerId,
            BookingState status
    );
}