package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {

        if (userRepository.findById(userId) == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new RuntimeException("Название вещи обязательно");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new RuntimeException("Описание вещи обязательно");
        }

        if (itemDto.getAvailable() == null) {
            throw new RuntimeException("Поле available обязательно");
        }

        Item item = new Item();

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
        item.setRequestId(itemDto.getRequestId());

        Item savedItem = itemRepository.save(item);

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId);

        if (existingItem == null) {
            return null;
        }

        if (existingItem.getOwnerId() != userId) {
            throw new RuntimeException("Изменять вещь может только её владелец");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.update(existingItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(long itemId) {
        Item item = itemRepository.findById(itemId);

        if (item == null) {
            return null;
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}