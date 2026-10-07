package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayDtoTest {

    @Test
    void dto_shouldStoreAllFields() {
        LocalDateTime start =
                LocalDateTime.of(2026, 10, 6, 10, 0);

        LocalDateTime end =
                LocalDateTime.of(2026, 10, 6, 12, 0);

        BookingShortDto shortDto = new BookingShortDto();
        shortDto.setId(1L);
        shortDto.setBookerId(2L);
        shortDto.setStart(start);
        shortDto.setEnd(end);

        assertThat(shortDto.getId()).isEqualTo(1L);
        assertThat(shortDto.getBookerId()).isEqualTo(2L);
        assertThat(shortDto.getStart()).isEqualTo(start);
        assertThat(shortDto.getEnd()).isEqualTo(end);

        UserDto user = new UserDto();
        user.setId(3L);
        user.setName("Александра");
        user.setEmail("alexandra@test.ru");

        assertThat(user.getId()).isEqualTo(3L);
        assertThat(user.getName()).isEqualTo("Александра");
        assertThat(user.getEmail()).isEqualTo("alexandra@test.ru");

        CommentDto comment = new CommentDto();
        comment.setId(4L);
        comment.setText("Хорошо");
        comment.setAuthorName("Александра");
        comment.setCreated(start);

        assertThat(comment.getId()).isEqualTo(4L);
        assertThat(comment.getText()).isEqualTo("Хорошо");
        assertThat(comment.getAuthorName())
                .isEqualTo("Александра");
        assertThat(comment.getCreated()).isEqualTo(start);

        ItemDto item = new ItemDto();
        item.setId(5L);
        item.setName("Дрель");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwnerId(3L);
        item.setRequestId(6L);
        item.setLastBooking(shortDto);
        item.setNextBooking(shortDto);
        item.setComments(List.of(comment));

        assertThat(item.getId()).isEqualTo(5L);
        assertThat(item.getName()).isEqualTo("Дрель");
        assertThat(item.getDescription()).isEqualTo("Описание");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwnerId()).isEqualTo(3L);
        assertThat(item.getRequestId()).isEqualTo(6L);
        assertThat(item.getLastBooking()).isEqualTo(shortDto);
        assertThat(item.getNextBooking()).isEqualTo(shortDto);
        assertThat(item.getComments()).containsExactly(comment);

        BookingDto booking = new BookingDto();
        booking.setId(7L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItemId(5L);

        assertThat(booking.getId()).isEqualTo(7L);
        assertThat(booking.getStart()).isEqualTo(start);
        assertThat(booking.getEnd()).isEqualTo(end);
        assertThat(booking.getItemId()).isEqualTo(5L);

        BookingResponseDto response = new BookingResponseDto();
        response.setId(8L);
        response.setStart(start);
        response.setEnd(end);
        response.setItem(item);
        response.setBooker(user);
        response.setStatus(BookingState.APPROVED);

        assertThat(response.getId()).isEqualTo(8L);
        assertThat(response.getStart()).isEqualTo(start);
        assertThat(response.getEnd()).isEqualTo(end);
        assertThat(response.getItem()).isEqualTo(item);
        assertThat(response.getBooker()).isEqualTo(user);
        assertThat(response.getStatus())
                .isEqualTo(BookingState.APPROVED);

        ItemRequestItemDto requestItem = new ItemRequestItemDto();
        requestItem.setId(9L);
        requestItem.setName("Штатив");
        requestItem.setOwnerId(3L);

        assertThat(requestItem.getId()).isEqualTo(9L);
        assertThat(requestItem.getName()).isEqualTo("Штатив");
        assertThat(requestItem.getOwnerId()).isEqualTo(3L);

        ItemRequestDto request = new ItemRequestDto();
        request.setId(10L);
        request.setDescription("Нужен штатив");
        request.setCreated(start);
        request.setItems(List.of(requestItem));

        assertThat(request.getId()).isEqualTo(10L);
        assertThat(request.getDescription())
                .isEqualTo("Нужен штатив");
        assertThat(request.getCreated()).isEqualTo(start);
        assertThat(request.getItems())
                .containsExactly(requestItem);
    }
}