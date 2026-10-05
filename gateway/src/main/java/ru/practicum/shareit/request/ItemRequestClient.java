package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Component
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ItemRequestDto createRequest(
            long userId,
            ItemRequestDto requestDto) {

        return postForUser(
                "/requests",
                userId,
                requestDto,
                ItemRequestDto.class
        );
    }

    public List<ItemRequestDto> getUserRequests(long userId) {

        return getForUser(
                "/requests",
                userId,
                new ParameterizedTypeReference<List<ItemRequestDto>>() {
                }
        );
    }

    public List<ItemRequestDto> getAllRequests(long userId) {

        return getForUser(
                "/requests/all",
                userId,
                new ParameterizedTypeReference<List<ItemRequestDto>>() {
                }
        );
    }

    public ItemRequestDto getRequest(long requestId) {

        return get(
                "/requests/" + requestId,
                ItemRequestDto.class
        );
    }
}