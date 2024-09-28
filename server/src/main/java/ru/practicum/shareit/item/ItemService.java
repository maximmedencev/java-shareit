package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentParamDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoParam;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto create(long sharerId, ItemDtoParam itemDtoParam);

    ItemDto read(long itemId);

    Collection<ItemDto> readAll(long sharerId);

    ItemDto update(long itemId, long sharerId, Item item);

    void delete(long sharerId, long itemId);

    Collection<ItemDto> search(long sharerId, String text);

    CommentDto createComment(long sharerId, long itemId, CommentParamDto commentParamDto);
}
