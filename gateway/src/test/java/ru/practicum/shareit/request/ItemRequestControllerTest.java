package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ActiveProfiles("test")
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Нужна дрель");
        requestDto.setCreated(LocalDateTime.of(2026, 10, 5, 20, 0));
        requestDto.setItems(List.of());

        when(itemRequestClient.createRequest(eq(1L), any(ItemRequestDto.class)))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ItemRequestDto() {{
                                    setDescription("Нужна дрель");
                                }}
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description")
                        .value("Нужна дрель"));
    }

    @Test
    void createRequest_shouldReturn400WhenDescriptionIsBlank()
            throws Exception {

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRequests_shouldReturnRequests() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Нужен велосипед");
        requestDto.setCreated(LocalDateTime.of(2026, 10, 5, 20, 0));
        requestDto.setItems(List.of());

        when(itemRequestClient.getUserRequests(1L))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description")
                        .value("Нужен велосипед"));
    }

    @Test
    void getAllRequests_shouldReturnRequests() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(2L);
        requestDto.setDescription("Нужен проектор");
        requestDto.setCreated(LocalDateTime.of(2026, 10, 5, 20, 0));
        requestDto.setItems(List.of());

        when(itemRequestClient.getAllRequests(1L))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].description")
                        .value("Нужен проектор"));
    }

    @Test
    void getRequest_shouldReturnRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setId(3L);
        requestDto.setDescription("Нужен штатив");
        requestDto.setCreated(LocalDateTime.of(2026, 10, 5, 20, 0));

        ItemRequestItemDto itemDto = new ItemRequestItemDto();
        itemDto.setId(10L);
        itemDto.setName("Штатив");
        itemDto.setOwnerId(2L);

        requestDto.setItems(List.of(itemDto));

        when(itemRequestClient.getRequest(3L))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.description")
                        .value("Нужен штатив"))
                .andExpect(jsonPath("$.items[0].id").value(10))
                .andExpect(jsonPath("$.items[0].name")
                        .value("Штатив"))
                .andExpect(jsonPath("$.items[0].ownerId").value(2));
    }
}