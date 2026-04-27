package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ru.practicum.shareit.common.exception.RepositoryException;

import org.springframework.stereotype.Repository;

@Repository
class ItemInMemoryRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long primaryKey = 1L;

    @Override
    public Item save(Item item) {
        if (item == null) {
            throw new RepositoryException("Cannot save null item");
        }
        item.setId(primaryKey++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (item == null) {
            throw new RepositoryException("Item is null");
        }
        if (!items.containsKey(item.getId())) {
            throw new RepositoryException("Item with id " + item.getId() + " not found");
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return Optional.of(items.get(id));
    }

    @Override
    public List<Item> findAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findItemsByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .toList();
    }

    @Override
    public List<Item> searchAvailableItems(String text) {
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> {
                    String fulltext = item.getName() + item.getDescription();
                    return fulltext.toLowerCase().contains(text.toLowerCase());
                })
                .toList();
    }
}
