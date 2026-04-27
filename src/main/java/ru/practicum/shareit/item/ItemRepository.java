package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

interface ItemRepository {

    Item save(Item item);

    Item update(Item item);

    Optional<Item> findItemById(Long id);

    List<Item> findAllItems();

    List<Item> findItemsByOwnerId(Long ownerId);

    List<Item> searchAvailableItems(String text);

}
