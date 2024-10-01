package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(item.getComments().stream().map(CommentMapper::mapToCommentDto).toList())
                .build();
    }

    public static ItemIdAndNameDto mapToItemIdAndNameDto(Item item) {

        return ItemIdAndNameDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static Item itemDtoParamToItem(ItemDtoParam itemDtoParam) {
        Item item = new Item();
        item.setName(itemDtoParam.getName());
        item.setDescription(itemDtoParam.getDescription());
        item.setAvailable(itemDtoParam.getAvailable());
        return item;
    }
}
