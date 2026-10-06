package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createUser_shouldSaveUser() {
        User user = new User();
        user.setName("Александра");
        user.setEmail("alexandra." + System.nanoTime() + "@test.ru");

        User saved = userService.createUser(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Александра");
        assertThat(saved.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User first = new User();
        first.setName("Первый");
        first.setEmail("first." + System.nanoTime() + "@test.ru");

        User second = new User();
        second.setName("Второй");
        second.setEmail("second." + System.nanoTime() + "@test.ru");

        userRepository.save(first);
        userRepository.save(second);

        assertThat(userService.getAllUsers()).hasSize(2);
    }

    @Test
    void getUserById_shouldReturnUser() {
        User user = new User();
        user.setName("Александра");
        user.setEmail("user." + System.nanoTime() + "@test.ru");

        User saved = userRepository.save(user);

        User result = userService.getUserById(saved.getId());

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getName()).isEqualTo("Александра");
    }

    @Test
    void updateUser_shouldChangeUser() {
        User user = new User();
        user.setName("Старое имя");
        user.setEmail("update." + System.nanoTime() + "@test.ru");

        User saved = userRepository.save(user);

        User update = new User();
        update.setName("Новое имя");

        User result = userService.updateUser(saved.getId(), update);

        assertThat(result.getName()).isEqualTo("Новое имя");
        assertThat(result.getEmail()).isEqualTo(saved.getEmail());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        User user = new User();
        user.setName("Удаляемый");
        user.setEmail("delete." + System.nanoTime() + "@test.ru");

        User saved = userRepository.save(user);

        userService.deleteUser(saved.getId());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void getUserById_shouldThrowForMissingUser() {
        assertThatThrownBy(() -> userService.getUserById(999999L))
                .isInstanceOf(Exception.class);
    }

}