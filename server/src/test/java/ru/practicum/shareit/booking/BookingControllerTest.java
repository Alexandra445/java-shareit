package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(bookingService).createBooking(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void approveBooking() throws Exception {
        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingService).approveBooking(1L, 1L, true);
    }

    @Test
    void getBooking() throws Exception {
        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingService).getBooking(1L, 1L);
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getUserBookings(1L, BookingQueryState.ALL))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingService).getUserBookings(1L, BookingQueryState.ALL);
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingService.getOwnerBookings(1L, BookingQueryState.ALL))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingService).getOwnerBookings(1L, BookingQueryState.ALL);
    }
}