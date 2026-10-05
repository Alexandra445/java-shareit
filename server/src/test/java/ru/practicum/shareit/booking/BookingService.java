package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

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
}