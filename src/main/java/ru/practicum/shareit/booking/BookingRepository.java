package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    Booking findFirstByItemIdAndEndBeforeAndStatusOrderByEndDesc(
            Long itemId,
            LocalDateTime now,
            BookingState status
    );

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId,
            LocalDateTime now,
            BookingState status
    );

    boolean existsByItemIdAndBookerIdAndEndBeforeAndStatus(
            Long itemId,
            Long bookerId,
            LocalDateTime now,
            BookingState status
    );
}