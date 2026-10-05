package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeAndDeserializeItemRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();

        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setCreated(LocalDateTime.of(2026, 10, 5, 20, 30));

        ItemRequestItemDto item = new ItemRequestItemDto();
        item.setId(10L);
        item.setName("Дрель");
        item.setOwnerId(2L);

        dto.setItems(List.of(item));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"description\":\"Нужна дрель\"");
        assertThat(json).contains("\"created\":\"2026-10-05T20:30:00\"");
        assertThat(json).contains("\"ownerId\":2");

        ItemRequestDto result =
                objectMapper.readValue(json, ItemRequestDto.class);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getCreated())
                .isEqualTo(LocalDateTime.of(2026, 10, 5, 20, 30));
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName())
                .isEqualTo("Дрель");
    }
}