package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.mockito.Mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingQueryState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GatewayClientsTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;
    private ItemClient itemClient;
    private BookingClient bookingClient;
    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient(restTemplate, "http://localhost:9090");
        itemClient = new ItemClient(restTemplate, "http://localhost:9090");
        bookingClient = new BookingClient(
                restTemplate,
                "http://localhost:9090"
        );
        itemRequestClient = new ItemRequestClient(
                restTemplate,
                "http://localhost:9090"
        );

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenAnswer(invocation ->
                ResponseEntity.<Object>ok(null));

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenAnswer(invocation ->
                ResponseEntity.ok(List.of()));
    }

    @Test
    void userClient_shouldExecuteAllMethods() {
        UserDto user = new UserDto();

        assertThat(userClient.create(user)).isNull();
        assertThat(userClient.getAll()).isEmpty();
        assertThat(userClient.getById(1L)).isNull();
        assertThat(userClient.update(1L, user)).isNull();

        userClient.delete(1L);
    }

    @Test
    void itemClient_shouldExecuteAllMethods() {
        ItemDto item = new ItemDto();
        CommentDto comment = new CommentDto();

        assertThat(itemClient.addItem(1L, item)).isNull();
        assertThat(itemClient.updateItem(1L, 2L, item)).isNull();
        assertThat(itemClient.getItem(2L)).isNull();
        assertThat(itemClient.getItems(1L)).isEmpty();
        assertThat(itemClient.searchItems("дрель")).isEmpty();
        assertThat(itemClient.addComment(1L, 2L, comment)).isNull();
    }

    @Test
    void bookingClient_shouldExecuteAllMethods() {
        BookingDto booking = new BookingDto();

        assertThat(bookingClient.createBooking(1L, booking))
                .isNull();

        assertThat(bookingClient.approveBooking(1L, 2L, true))
                .isNull();

        assertThat(bookingClient.getBooking(1L, 2L))
                .isNull();

        assertThat(bookingClient.getUserBookings(
                1L,
                BookingQueryState.ALL
        )).isEmpty();

        assertThat(bookingClient.getOwnerBookings(
                1L,
                BookingQueryState.ALL
        )).isEmpty();
    }

    @Test
    void itemRequestClient_shouldExecuteAllMethods() {
        ItemRequestDto request = new ItemRequestDto();

        assertThat(itemRequestClient.createRequest(1L, request))
                .isNull();

        assertThat(itemRequestClient.getUserRequests(1L))
                .isEmpty();

        assertThat(itemRequestClient.getAllRequests(1L))
                .isEmpty();

        assertThat(itemRequestClient.getRequest(1L))
                .isNull();
    }
}