package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final List<Item> items = new ArrayList<>();
    private long nextId = 1;

    @Override
    public Item save(Item item) {
        item.setId(nextId++);
        items.add(item);
        return item;
    }

    @Override
    public Item findById(long itemId) {
        return items.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Item> findByOwnerId(long ownerId) {
        return items.stream()
                .filter(item -> item.getOwnerId() == ownerId)
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        String searchText = text.toLowerCase();

        return items.stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(searchText)
                                || item.getDescription().toLowerCase().contains(searchText))
                .toList();
    }

    @Override
    public Item update(Item item) {
        Item existingItem = findById(item.getId());

        if (existingItem != null) {
            existingItem.setName(item.getName());
            existingItem.setDescription(item.getDescription());
            existingItem.setAvailable(item.getAvailable());
        }

        return existingItem;
    }
}