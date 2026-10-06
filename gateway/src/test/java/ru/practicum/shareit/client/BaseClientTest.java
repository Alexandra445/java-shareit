package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseClientTest {

    private static final String SERVER_URL = "http://localhost:9090";

    @Mock
    private RestTemplate restTemplate;

    private TestClient client;

    @BeforeEach
    void setUp() {
        client = new TestClient(restTemplate, SERVER_URL);
    }

    @Test
    void classRequests_shouldWork() {
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenAnswer(invocation -> ResponseEntity.<Object>ok(null));

        assertThat(client.getValue()).isNull();
        assertThat(client.getValueForUser(10L)).isNull();
        assertThat(client.postValue()).isNull();
        assertThat(client.postValueForUser(10L)).isNull();
        assertThat(client.patchValue(10L)).isNull();
        assertThat(client.patchValueWithoutUser()).isNull();

        client.deleteValue();

        ArgumentCaptor<HttpEntity> captor =
                ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(
                eq(SERVER_URL + "/get-user"),
                eq(HttpMethod.GET),
                captor.capture(),
                eq(String.class)
        );

        assertThat(captor.getValue()
                .getHeaders()
                .getFirst("X-Sharer-User-Id"))
                .isEqualTo("10");
    }

    @Test
    void parameterizedRequests_shouldWork() {
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenAnswer(invocation -> ResponseEntity.ok(List.of("value")));

        assertThat(client.getList())
                .containsExactly("value");

        assertThat(client.getListForUser(10L))
                .containsExactly("value");
    }

    @Test
    void classRequest_shouldConvertHttpError() {
        HttpClientErrorException error =
                HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        HttpHeaders.EMPTY,
                        "Ошибка".getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(error);

        assertThatThrownBy(() -> client.getValue())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ошибка");
    }

    @Test
    void parameterizedRequest_shouldConvertHttpError() {
        HttpClientErrorException error =
                HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        HttpHeaders.EMPTY,
                        "Ошибка списка".getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8
                );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class)
        )).thenThrow(error);

        assertThatThrownBy(() -> client.getList())
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ошибка списка");
    }

    private static class TestClient extends BaseClient {

        TestClient(RestTemplate restTemplate, String serverUrl) {
            super(restTemplate, serverUrl);
        }

        String getValue() {
            return get("/get", String.class);
        }

        String getValueForUser(long userId) {
            return getForUser("/get-user", userId, String.class);
        }

        List<String> getList() {
            return get(
                    "/list",
                    new ParameterizedTypeReference<List<String>>() {
                    }
            );
        }

        List<String> getListForUser(long userId) {
            return getForUser(
                    "/list-user",
                    userId,
                    new ParameterizedTypeReference<List<String>>() {
                    }
            );
        }

        String postValue() {
            return post("/post", "body", String.class);
        }

        String postValueForUser(long userId) {
            return postForUser(
                    "/post-user",
                    userId,
                    "body",
                    String.class
            );
        }

        String patchValue(long userId) {
            return patch(
                    "/patch",
                    userId,
                    "body",
                    String.class
            );
        }

        String patchValueWithoutUser() {
            return patch(
                    "/patch-no-user",
                    "body",
                    String.class
            );
        }

        void deleteValue() {
            delete("/delete");
        }
    }
}