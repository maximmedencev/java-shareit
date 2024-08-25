package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long sharerId, Item item);

    ItemDto read(long itemId);

    Collection<ItemDto> readAll(long sharerId);

    ItemDto update(long itemId, long sharerId, Item item);

    void delete(long sharerId, long itemId);

    Collection<ItemDto> search(long sharerId, String text);
}
