package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(long userId, BookingDto bookingDto);

    BookingDto approveBooking(long ownerId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getUserBookings(long userId, BookingQueryState state);

    List<BookingDto> getOwnerBookings(long ownerId, BookingQueryState state);
}