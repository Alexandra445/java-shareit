package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;

@Component
public class BookingClient extends BaseClient {

    public BookingClient(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public BookingResponseDto createBooking(
            long userId,
            BookingDto bookingDto) {

        return postForUser(
                "/bookings",
                userId,
                bookingDto,
                BookingResponseDto.class
        );
    }

    public BookingResponseDto approveBooking(
            long userId,
            long bookingId,
            boolean approved) {

        return patch(
                "/bookings/" + bookingId + "?approved=" + approved,
                userId,
                null,
                BookingResponseDto.class
        );
    }

    public BookingResponseDto getBooking(
            long userId,
            long bookingId) {

        return getForUser(
                "/bookings/" + bookingId,
                userId,
                BookingResponseDto.class
        );
    }

    public List<BookingResponseDto> getUserBookings(
            long userId,
            BookingQueryState state) {

        return getForUser(
                "/bookings?state=" + state,
                userId,
                new ParameterizedTypeReference<List<BookingResponseDto>>() {
                }
        );
    }

    public List<BookingResponseDto> getOwnerBookings(
            long userId,
            BookingQueryState state) {

        return getForUser(
                "/bookings/owner?state=" + state,
                userId,
                new ParameterizedTypeReference<List<BookingResponseDto>>() {
                }
        );
    }
}