package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner." + System.nanoTime() + "@test.ru");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Букер");
        booker.setEmail("booker." + System.nanoTime() + "@test.ru");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Дрель");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);
    }

    @Test
    void createBooking_shouldSaveBooking() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        BookingResponseDto result =
                bookingService.createBooking(booker.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus())
                .isEqualTo(BookingState.WAITING);
    }

    @Test
    void approveBooking_shouldApproveBooking() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        BookingResponseDto created =
                bookingService.createBooking(booker.getId(), dto);

        BookingResponseDto result =
                bookingService.approveBooking(
                        owner.getId(),
                        created.getId(),
                        true
                );

        assertThat(result.getStatus())
                .isEqualTo(BookingState.APPROVED);
    }

    @Test
    void getBooking_shouldReturnBookingForBooker() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        BookingResponseDto created =
                bookingService.createBooking(booker.getId(), dto);

        BookingResponseDto result =
                bookingService.getBooking(
                        booker.getId(),
                        created.getId()
                );

        assertThat(result.getId())
                .isEqualTo(created.getId());
    }

    @Test
    void getUserBookings_shouldReturnBookerBookings() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        bookingService.createBooking(booker.getId(), dto);

        assertThat(
                bookingService.getUserBookings(
                        booker.getId(),
                        BookingQueryState.ALL
                )
        ).hasSize(1);
    }

    @Test
    void getOwnerBookings_shouldReturnOwnerBookings() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        bookingService.createBooking(booker.getId(), dto);

        assertThat(
                bookingService.getOwnerBookings(
                        owner.getId(),
                        BookingQueryState.ALL
                )
        ).hasSize(1);
    }

    @Test
    void createBooking_shouldFailForMissingUser() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        assertThatThrownBy(() ->
                bookingService.createBooking(999999L, dto))
                .isInstanceOfSatisfying(ResponseStatusException.class, e ->
                        assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createBooking_shouldFailForMissingItem() {
        BookingDto dto = new BookingDto();
        dto.setItemId(999999L);
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        assertThatThrownBy(() ->
                bookingService.createBooking(booker.getId(), dto))
                .isInstanceOfSatisfying(ResponseStatusException.class, e ->
                        assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void createBooking_shouldFailForOwnItem() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        assertThatThrownBy(() ->
                bookingService.createBooking(owner.getId(), dto))
                .isInstanceOf(Exception.class);
    }

    @Test
    void createBooking_shouldFailForUnavailableItem() {
        item.setAvailable(false);
        itemRepository.save(item);

        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        assertThatThrownBy(() ->
                bookingService.createBooking(booker.getId(), dto))
                .isInstanceOf(Exception.class);
    }

    @Test
    void createBooking_shouldFailForInvalidDates() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(4));
        dto.setEnd(LocalDateTime.now().plusHours(2));

        assertThatThrownBy(() ->
                bookingService.createBooking(booker.getId(), dto))
                .isInstanceOf(Exception.class);
    }

    @Test
    void createBooking_shouldFailForPastStart() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().minusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(2));

        assertThatThrownBy(() ->
                bookingService.createBooking(booker.getId(), dto))
                .isInstanceOf(Exception.class);
    }

    @Test
    void approveBooking_shouldRejectBooking() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        BookingResponseDto created =
                bookingService.createBooking(booker.getId(), dto);

        BookingResponseDto result =
                bookingService.approveBooking(
                        owner.getId(),
                        created.getId(),
                        false
                );

        assertThat(result.getStatus())
                .isEqualTo(BookingState.REJECTED);
    }

    @Test
    void approveBooking_shouldFailForAnotherUser() {
        User anotherUser = new User();
        anotherUser.setName("Другой");
        anotherUser.setEmail("another." + System.nanoTime() + "@test.ru");
        anotherUser = userRepository.save(anotherUser);

        Long anotherUserId = anotherUser.getId();

        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        BookingResponseDto created =
                bookingService.createBooking(booker.getId(), dto);

        assertThatThrownBy(() ->
                bookingService.approveBooking(
                        anotherUserId,
                        created.getId(),
                        true
                ))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getBooking_shouldReturnBookingForOwner() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        BookingResponseDto created =
                bookingService.createBooking(booker.getId(), dto);

        BookingResponseDto result =
                bookingService.getBooking(
                        owner.getId(),
                        created.getId()
                );

        assertThat(result.getId())
                .isEqualTo(created.getId());
    }

    @Test
    void getBooking_shouldFailForAnotherUser() {
        User anotherUser = new User();
        anotherUser.setName("Другой");
        anotherUser.setEmail("another." + System.nanoTime() + "@test.ru");
        anotherUser = userRepository.save(anotherUser);

        Long anotherUserId = anotherUser.getId();

        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusHours(2));
        dto.setEnd(LocalDateTime.now().plusHours(4));

        BookingResponseDto created =
                bookingService.createBooking(booker.getId(), dto);

        assertThatThrownBy(() ->
                bookingService.getBooking(
                        anotherUserId,
                        created.getId()
                ))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getUserBookings_shouldCoverAllStates() {
        bookingService.getUserBookings(booker.getId(), null);

        for (BookingQueryState state : BookingQueryState.values()) {
            bookingService.getUserBookings(booker.getId(), state);
        }
    }

    @Test
    void getOwnerBookings_shouldCoverAllStates() {
        bookingService.getOwnerBookings(owner.getId(), null);

        for (BookingQueryState state : BookingQueryState.values()) {
            bookingService.getOwnerBookings(owner.getId(), state);
        }
    }

    @Test
    void getUserBookings_shouldFailForMissingUser() {
        assertThatThrownBy(() ->
                bookingService.getUserBookings(
                        999999L,
                        BookingQueryState.ALL
                ))
                .isInstanceOf(Exception.class);
    }

    @Test
    void getOwnerBookings_shouldFailForMissingUser() {
        assertThatThrownBy(() ->
                bookingService.getOwnerBookings(
                        999999L,
                        BookingQueryState.ALL
                ))
                .isInstanceOf(Exception.class);
    }
}