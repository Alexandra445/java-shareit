package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        owner = new User();
        owner.setName("Владелец");
        owner.setEmail("owner." + System.nanoTime() + "@test.ru");
        owner = userRepository.save(owner);
    }

    @Test
    void addNewItem_shouldSaveItem() {
        ItemDto dto = new ItemDto();
        dto.setName("Дрель");
        dto.setDescription("Хорошая дрель");
        dto.setAvailable(true);

        ItemDto result = itemService.addNewItem(owner.getId(), dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Хорошая дрель");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void updateItem_shouldChangeItem() {
        Item item = new Item();
        item.setName("Старое название");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(owner);

        item = itemRepository.save(item);

        ItemDto update = new ItemDto();
        update.setName("Новое название");

        ItemDto result = itemService.updateItem(
                owner.getId(),
                item.getId(),
                update
        );

        assertThat(result.getName()).isEqualTo("Новое название");
    }

    @Test
    void getItem_shouldReturnItem() {
        Item item = new Item();
        item.setName("Шуруповёрт");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(owner);

        item = itemRepository.save(item);

        ItemDto result = itemService.getItem(item.getId());

        assertThat(result.getId()).isEqualTo(item.getId());
        assertThat(result.getName()).isEqualTo("Шуруповёрт");
    }

    @Test
    void getItems_shouldReturnOwnerItems() {
        Item first = new Item();
        first.setName("Дрель");
        first.setDescription("Первая");
        first.setAvailable(true);
        first.setOwner(owner);

        Item second = new Item();
        second.setName("Лобзик");
        second.setDescription("Вторая");
        second.setAvailable(true);
        second.setOwner(owner);

        itemRepository.save(first);
        itemRepository.save(second);

        assertThat(itemService.getItems(owner.getId()))
                .hasSize(2);
    }

    @Test
    void searchItems_shouldReturnAvailableItems() {
        Item item = new Item();
        item.setName("Дрель Bosch");
        item.setDescription("Электрическая");
        item.setAvailable(true);
        item.setOwner(owner);

        itemRepository.save(item);

        assertThat(itemService.searchItems("Bosch"))
                .hasSize(1);
    }

    @Test
    void addNewItem_withRequestId_shouldLinkItemToRequest() {
        ItemRequest request = new ItemRequest();
        request.setDescription("Нужен фотоаппарат");
        request.setCreated(java.time.LocalDateTime.now());
        request.setRequester(owner);

        ItemRequest savedRequest =
                itemRequestRepository.save(request);

        ItemDto dto = new ItemDto();
        dto.setName("Фотоаппарат");
        dto.setDescription("Зеркальный");
        dto.setAvailable(true);
        dto.setRequestId(savedRequest.getId());

        ItemDto result =
                itemService.addNewItem(owner.getId(), dto);

        Item savedItem =
                itemRepository.findById(result.getId())
                        .orElseThrow();

        assertThat(savedItem.getRequestId())
                .isEqualTo(savedRequest.getId());
    }
}