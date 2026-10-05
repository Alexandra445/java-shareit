package ru.practicum.shareit.item;

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

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    void addItem() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(itemService).addNewItem(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void updateItem() throws Exception {
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(itemService).updateItem(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void getItem() throws Exception {
        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk());

        verify(itemService).getItem(1L);
    }

    @Test
    void addComment() throws Exception {
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Отличный комментарий\"}"))
                .andExpect(status().isOk());

        verify(itemService).addComment(1L, 1L, "Отличный комментарий");
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(1L))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(itemService).getItems(1L);
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems("phone"))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/items/search")
                        .param("text", "phone"))
                .andExpect(status().isOk());

        verify(itemService).searchItems("phone");
    }
}