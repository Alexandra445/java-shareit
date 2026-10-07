package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

public class BaseClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public BaseClient(
            RestTemplate restTemplate,
            @Value("${shareit-server.url}") String serverUrl) {
        this.restTemplate = restTemplate;
        this.serverUrl = serverUrl;
    }

    protected <T> T get(String path, Class<T> responseType) {
        return sendRequest(
                HttpMethod.GET,
                path,
                null,
                responseType,
                null
        );
    }

    protected <T> T get(
            String path,
            ParameterizedTypeReference<T> responseType) {

        return sendRequest(
                HttpMethod.GET,
                path,
                null,
                responseType,
                null
        );
    }

    protected <T> T getForUser(
            String path,
            long userId,
            Class<T> responseType) {

        return sendRequest(
                HttpMethod.GET,
                path,
                null,
                responseType,
                userId
        );
    }

    protected <T> T getForUser(
            String path,
            long userId,
            ParameterizedTypeReference<T> responseType) {

        return sendRequest(
                HttpMethod.GET,
                path,
                null,
                responseType,
                userId
        );
    }

    protected <T> T post(
            String path,
            Object body,
            Class<T> responseType) {

        return sendRequest(
                HttpMethod.POST,
                path,
                body,
                responseType,
                null
        );
    }

    protected <T> T postForUser(
            String path,
            long userId,
            Object body,
            Class<T> responseType) {

        return sendRequest(
                HttpMethod.POST,
                path,
                body,
                responseType,
                userId
        );
    }

    protected <T> T patch(
            String path,
            long userId,
            Object body,
            Class<T> responseType) {

        return sendRequest(
                HttpMethod.PATCH,
                path,
                body,
                responseType,
                userId
        );
    }

    protected void delete(String path) {
        sendRequest(
                HttpMethod.DELETE,
                path,
                null,
                Void.class,
                null
        );
    }

    private <T> T sendRequest(
            HttpMethod method,
            String path,
            Object body,
            Class<T> responseType,
            Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    serverUrl + path,
                    method,
                    requestEntity,
                    responseType
            );

            return response.getBody();

        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e
            );
        }
    }

    private <T> T sendRequest(
            HttpMethod method,
            String path,
            Object body,
            ParameterizedTypeReference<T> responseType,
            Long userId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                    serverUrl + path,
                    method,
                    requestEntity,
                    responseType
            );

            return response.getBody();

        } catch (HttpStatusCodeException e) {
            throw new ResponseStatusException(
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    e
            );
        }
    }

    protected <T> T patch(
            String path,
            Object body,
            Class<T> responseType) {

        return sendRequest(
                HttpMethod.PATCH,
                path,
                body,
                responseType,
                null
        );
    }
}