package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ItemDto addItem(long userId, ItemDto itemDto) {
        return postForUser(
                "/items",
                userId,
                itemDto,
                ItemDto.class
        );
    }

    public ItemDto updateItem(
            long userId,
            long itemId,
            ItemDto itemDto) {

        return patch(
                "/items/" + itemId,
                userId,
                itemDto,
                ItemDto.class
        );
    }

    public ItemDto getItem(long itemId) {
        return get(
                "/items/" + itemId,
                ItemDto.class
        );
    }

    public List<ItemDto> getItems(long userId) {
        return getForUser(
                "/items",
                userId,
                new ParameterizedTypeReference<List<ItemDto>>() {
                }
        );
    }

    public List<ItemDto> searchItems(String text) {
        String path = UriComponentsBuilder
                .fromPath("/items/search")
                .queryParam("text", text)
                .build()
                .toUriString();

        return get(
                path,
                new ParameterizedTypeReference<List<ItemDto>>() {
                }
        );
    }

    public CommentDto addComment(
            long userId,
            long itemId,
            CommentDto commentDto) {

        return postForUser(
                "/items/" + itemId + "/comment",
                userId,
                commentDto,
                CommentDto.class
        );
    }
}