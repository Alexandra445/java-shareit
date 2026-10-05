package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.List;

@Component
public class UserClient extends BaseClient {

    public UserClient(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public UserDto create(UserDto userDto) {
        return post(
                "/users",
                userDto,
                UserDto.class
        );
    }

    public List<UserDto> getAll() {
        return get(
                "/users",
                new ParameterizedTypeReference<List<UserDto>>() {
                }
        );
    }

    public UserDto getById(long userId) {
        return get(
                "/users/" + userId,
                UserDto.class
        );
    }

    public UserDto update(long userId, UserDto userDto) {
        return patch(
                "/users/" + userId,
                userDto,
                UserDto.class
        );
    }

    public void delete(long userId) {
        delete("/users/" + userId);
    }
}