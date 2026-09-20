package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody BookingDto bookingDto) {

        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId,
            @RequestParam boolean approved) {

        return bookingService.approveBooking(
                userId,
                bookingId,
                approved
        );
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId) {

        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingQueryState state) {

        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL") BookingQueryState state) {

        return bookingService.getOwnerBookings(userId, state);
    }
}