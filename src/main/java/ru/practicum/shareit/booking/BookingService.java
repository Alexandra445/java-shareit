package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(
            long userId,
            BookingDto bookingDto);

    BookingResponseDto approveBooking(
            long ownerId,
            long bookingId,
            boolean approved);

    BookingResponseDto getBooking(
            long userId,
            long bookingId);

    List<BookingResponseDto> getUserBookings(
            long userId,
            BookingQueryState state);

    List<BookingResponseDto> getOwnerBookings(
            long ownerId,
            BookingQueryState state);
}