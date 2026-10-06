package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void addItem_shouldReturnItem() throws Exception {
        ItemDto input = new ItemDto();
        input.setName("Дрель");
        input.setDescription("Хорошая дрель");
        input.setAvailable(true);

        ItemDto result = new ItemDto();
        result.setId(1L);
        result.setName("Дрель");
        result.setDescription("Хорошая дрель");
        result.setAvailable(true);

        when(itemClient.addItem(1L, input)).thenReturn(result);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void addItem_shouldReturn400ForInvalidData() throws Exception {
        ItemDto input = new ItemDto();
        input.setName("");
        input.setDescription("");
        input.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        ItemDto input = new ItemDto();
        input.setName("Новое название");

        ItemDto result = new ItemDto();
        result.setId(1L);
        result.setName("Новое название");

        when(itemClient.updateItem(
                1L,
                1L,
                input
        )).thenReturn(result);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value("Новое название"));
    }

    @Test
    void getItem_shouldReturnItem() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Дрель");

        when(itemClient.getItem(1L)).thenReturn(item);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void getItems_shouldReturnItems() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Дрель");

        when(itemClient.getItems(1L))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void searchItems_shouldReturnItems() throws Exception {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("Дрель");

        when(itemClient.searchItems("дрель"))
                .thenReturn(List.of(item));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void addComment_shouldReturnComment() throws Exception {
        CommentDto input = new CommentDto();
        input.setText("Отличная вещь!");

        CommentDto result = new CommentDto();
        result.setId(1L);
        result.setText("Отличная вещь!");

        when(itemClient.addComment(
                1L,
                1L,
                input
        )).thenReturn(result);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text")
                        .value("Отличная вещь!"));
    }
}