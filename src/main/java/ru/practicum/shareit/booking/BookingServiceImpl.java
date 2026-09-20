package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto createBooking(long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Пользователь не найден"
                        )
                );

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Вещь не найдена"
                        )
                );

        if (item.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Нельзя забронировать собственную вещь"
            );
        }

        if (!item.getAvailable()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Вещь недоступна для бронирования"
            );
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Дата начала и окончания бронирования обязательны"
            );
        }

        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Дата начала должна быть раньше даты окончания"
            );
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Дата начала бронирования не может быть в прошлом"
            );
        }

        Booking booking = new Booking();

        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingState.WAITING);

        return toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long ownerId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Бронирование не найдено"
                        )
                );

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Только владелец вещи может подтвердить или отклонить бронирование"
            );
        }

        booking.setStatus(approved
                ? BookingState.APPROVED
                : BookingState.REJECTED);

        return toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Бронирование не найдено"
                        )
                );

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Нет доступа к этому бронированию"
            );
        }

        return toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(long userId, BookingQueryState state) {
        checkUserExists(userId);

        List<Booking> bookings =
                bookingRepository.findByBookerIdOrderByStartDesc(userId);

        return filterByState(bookings, state)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(long ownerId, BookingQueryState state) {
        checkUserExists(ownerId);

        List<Booking> bookings =
                bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);

        return filterByState(bookings, state)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private List<Booking> filterByState(
            List<Booking> bookings,
            BookingQueryState state) {

        if (state == null || state == BookingQueryState.ALL) {
            return bookings;
        }

        LocalDateTime now = LocalDateTime.now();

        return switch (state) {
            case CURRENT -> bookings.stream()
                    .filter(booking ->
                            !booking.getStart().isAfter(now)
                                    && booking.getEnd().isAfter(now))
                    .toList();

            case PAST -> bookings.stream()
                    .filter(booking ->
                            !booking.getEnd().isAfter(now))
                    .toList();

            case FUTURE -> bookings.stream()
                    .filter(booking ->
                            booking.getStart().isAfter(now))
                    .toList();

            case WAITING -> bookings.stream()
                    .filter(booking ->
                            booking.getStatus() == BookingState.WAITING)
                    .toList();

            case REJECTED -> bookings.stream()
                    .filter(booking ->
                            booking.getStatus() == BookingState.REJECTED)
                    .toList();

            case ALL -> bookings;
        };
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Пользователь не найден"
            );
        }
    }

    private BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setItemId(booking.getItem().getId());
        dto.setBookerId(booking.getBooker().getId());
        dto.setStatus(booking.getStatus());

        return dto;
    }
}