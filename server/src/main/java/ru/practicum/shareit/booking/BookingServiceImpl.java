package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
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
    public BookingResponseDto createBooking(
            long userId,
            BookingDto bookingDto) {
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
    public BookingResponseDto approveBooking(
            long ownerId,
            long bookingId,
            boolean approved) {
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
    public BookingResponseDto getBooking(
            long userId,
            long bookingId) {
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
    public List<BookingResponseDto> getUserBookings(
            long userId,
            BookingQueryState state) {
        checkUserExists(userId);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        if (state == null || state == BookingQueryState.ALL) {
            bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
        } else {
            bookings = switch (state) {
                case CURRENT ->
                        bookingRepository
                                .findByBookerIdAndStartLessThanEqualAndEndAfterOrderByStartDesc(
                                        userId, now, now);

                case PAST ->
                        bookingRepository
                                .findByBookerIdAndEndLessThanEqualOrderByStartDesc(
                                        userId, now);

                case FUTURE ->
                        bookingRepository
                                .findByBookerIdAndStartAfterOrderByStartDesc(
                                        userId, now);

                case WAITING ->
                        bookingRepository
                                .findByBookerIdAndStatusOrderByStartDesc(
                                        userId, BookingState.WAITING);

                case REJECTED ->
                        bookingRepository
                                .findByBookerIdAndStatusOrderByStartDesc(
                                        userId, BookingState.REJECTED);

                case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            };
        }

        return bookings.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getOwnerBookings(
            long ownerId,
            BookingQueryState state) {
        checkUserExists(ownerId);

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        if (state == null || state == BookingQueryState.ALL) {
            bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
        } else {
            bookings = switch (state) {
                case CURRENT ->
                        bookingRepository
                                .findByItemOwnerIdAndStartLessThanEqualAndEndAfterOrderByStartDesc(
                                        ownerId, now, now);

                case PAST ->
                        bookingRepository
                                .findByItemOwnerIdAndEndLessThanEqualOrderByStartDesc(
                                        ownerId, now);

                case FUTURE ->
                        bookingRepository
                                .findByItemOwnerIdAndStartAfterOrderByStartDesc(
                                        ownerId, now);

                case WAITING ->
                        bookingRepository
                                .findByItemOwnerIdAndStatusOrderByStartDesc(
                                        ownerId, BookingState.WAITING);

                case REJECTED ->
                        bookingRepository
                                .findByItemOwnerIdAndStatusOrderByStartDesc(
                                        ownerId, BookingState.REJECTED);

                case ALL ->
                        bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            };
        }

        return bookings.stream()
                .map(this::toDto)
                .toList();
    }

    private void checkUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Пользователь не найден"
            );
        }
    }

    private BookingResponseDto toDto(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(booking.getItem().getId());
        itemDto.setName(booking.getItem().getName());

        dto.setItem(itemDto);

        UserDto bookerDto = new UserDto();
        bookerDto.setId(booking.getBooker().getId());
        bookerDto.setName(booking.getBooker().getName());
        bookerDto.setEmail(booking.getBooker().getEmail());

        dto.setBooker(bookerDto);

        dto.setStatus(booking.getStatus());

        return dto;
    }
}