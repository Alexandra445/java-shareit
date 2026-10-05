package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Александра");
        user.setEmail("alexandra." + System.nanoTime() + "@test.ru");
        user = userRepository.save(user);

        anotherUser = new User();
        anotherUser.setName("Иван");
        anotherUser.setEmail("ivan." + System.nanoTime() + "@test.ru");
        anotherUser = userRepository.save(anotherUser);
    }

    @Test
    void createRequest_shouldSaveRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужна дрель");

        ItemRequestDto result =
                itemRequestService.createRequest(user.getId(), requestDto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getCreated()).isNotNull();
        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void getUserRequests_shouldReturnOnlyUserRequests() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен велосипед");

        itemRequestService.createRequest(user.getId(), requestDto);

        List<ItemRequestDto> result =
                itemRequestService.getUserRequests(user.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription())
                .isEqualTo("Нужен велосипед");
    }

    @Test
    void getAllRequests_shouldReturnRequestsOfOtherUsers() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен проектор");

        itemRequestService.createRequest(user.getId(), requestDto);

        List<ItemRequestDto> result =
                itemRequestService.getAllRequests(anotherUser.getId());

        assertThat(result)
                .extracting(ItemRequestDto::getDescription)
                .contains("Нужен проектор");
    }

    @Test
    void getRequest_shouldReturnRequestById() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Нужен штатив");

        ItemRequestDto created =
                itemRequestService.createRequest(user.getId(), requestDto);

        ItemRequestDto result =
                itemRequestService.getRequest(created.getId());

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getDescription())
                .isEqualTo("Нужен штатив");
    }
}