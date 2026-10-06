package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import ru.practicum.Main;
import ru.practicum.shareit.booking.BookingQueryState;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.client.RestTemplateConfig;

import static org.assertj.core.api.Assertions.assertThat;

class GatewayInfrastructureTest {

    @Test
    void restTemplateConfig_shouldCreateRestTemplate() {
        RestTemplateConfig config = new RestTemplateConfig();

        RestTemplate result = config.restTemplate();

        assertThat(result).isNotNull();
    }

    @Test
    void bookingStates_shouldContainAllValues() {
        assertThat(BookingState.values())
                .contains(
                        BookingState.WAITING,
                        BookingState.APPROVED,
                        BookingState.REJECTED,
                        BookingState.CANCELED
                );

        assertThat(BookingQueryState.values())
                .contains(
                        BookingQueryState.ALL,
                        BookingQueryState.CURRENT,
                        BookingQueryState.PAST,
                        BookingQueryState.FUTURE,
                        BookingQueryState.WAITING,
                        BookingQueryState.REJECTED
                );
    }

    @Test
    void main_shouldRun() {
        Main.main(new String[0]);
    }
}