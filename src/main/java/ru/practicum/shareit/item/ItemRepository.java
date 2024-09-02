package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Set;

public interface ItemRepository {
    Item read(long itemId);

    Long getOwnerId(Long itemId);

    Item create(Item item);

    Collection<Item> readAll(long sharerId);

    Item update(Item item);

    void delete(long itemId);

    Set<Item> search(String text);

}
