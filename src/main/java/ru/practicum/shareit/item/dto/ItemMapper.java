package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId() != null ? item.getId() : null)
                .name(item.getName() != null ? item.getName() : null)
                .description(item.getDescription() != null ? item.getDescription() : null)
                .available(item.getAvailable() != null ? item.getAvailable() : null)
                .owner(item.getOwner() != null ? item.getOwner() : null)
                .request(item.getRequest() != null ? item.getRequest() : null)
                .build();
    }

    public static Item mapToItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId() != null ? itemDto.getId() : null)
                .name(itemDto.getName() != null ? itemDto.getName() : null)
                .description(itemDto.getDescription() != null ? itemDto.getDescription() : null)
                .available(itemDto.getAvailable() != null ? itemDto.getAvailable() : null)
                .owner(itemDto.getOwner() != null ? itemDto.getOwner() : null)
                .request(itemDto.getRequest() != null ? itemDto.getRequest() : null)
                .build();
    }
}
