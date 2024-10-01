package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserIdOnlyDto;

import java.util.ArrayList;

public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequestor(new UserIdOnlyDto(itemRequest.getRequestor().getId()));
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(itemRequest.getItems()
                .stream()
                .map(ItemMapper::mapToItemIdAndNameDto)
                .toList());
        return itemRequestDto;
    }

    public static ItemRequest mapToItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        User requestor = new User();
        requestor.setId(itemRequestDto.getId());
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }
}