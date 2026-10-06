package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ActiveProfiles("test")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createBooking_shouldReturnBooking() throws Exception {
        BookingDto input = new BookingDto();
        input.setItemId(1L);
        input.setStart(LocalDateTime.of(2026, 10, 6, 10, 0));
        input.setEnd(LocalDateTime.of(2026, 10, 6, 12, 0));

        BookingResponseDto result = new BookingResponseDto();
        result.setId(1L);
        result.setStart(input.getStart());
        result.setEnd(input.getEnd());

        when(bookingClient.createBooking(1L, input))
                .thenReturn(result);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createBooking_shouldReturn400ForInvalidData() throws Exception {
        BookingDto input = new BookingDto();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_shouldReturnBooking() throws Exception {
        BookingResponseDto result = new BookingResponseDto();
        result.setId(1L);

        when(bookingClient.approveBooking(1L, 1L, true))
                .thenReturn(result);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        BookingResponseDto result = new BookingResponseDto();
        result.setId(1L);

        when(bookingClient.getBooking(1L, 1L))
                .thenReturn(result);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getUserBookings_shouldReturnBookings() throws Exception {
        BookingResponseDto result = new BookingResponseDto();
        result.setId(1L);

        when(bookingClient.getUserBookings(
                1L,
                BookingQueryState.ALL
        )).thenReturn(List.of(result));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getOwnerBookings_shouldReturnBookings() throws Exception {
        BookingResponseDto result = new BookingResponseDto();
        result.setId(1L);

        when(bookingClient.getOwnerBookings(
                1L,
                BookingQueryState.ALL
        )).thenReturn(List.of(result));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}