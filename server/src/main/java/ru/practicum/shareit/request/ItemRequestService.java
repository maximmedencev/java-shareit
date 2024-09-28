package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequest findById(Long id);

    ItemRequestDto save(long sharerId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getAllRequestsOfUsers(long sharerId);

    Collection<ItemRequestDto> getAllUserRequests(long sharerId);

    ItemRequestDto getRequest(long requestId);
}
