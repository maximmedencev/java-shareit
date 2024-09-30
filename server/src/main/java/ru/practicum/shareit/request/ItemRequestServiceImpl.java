package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requests;
    private final UserRepository users;
    private final ItemRepository items;

    @Override
    public ItemRequestDto save(long sharerId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);
        Optional<User> optionalRequestor = users.findById(sharerId);
        if (optionalRequestor.isEmpty()) {
            log.error("Пользователь с id = {} не найден", sharerId);
            throw new NotFoundException("Пользователь с id = " + sharerId + " не найден");
        }
        itemRequest.setRequestor(optionalRequestor.get());
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.mapToItemRequestDto(requests.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestDto> getAllRequestsOfUsers(long sharerId) {
        if (!users.existsById(sharerId)) {
            log.error("Пользователь с id = {} не найден", sharerId);
            throw new WrongUserException("Пользователь с id = " + sharerId + " не найден");
        }
        return requests.findByRequestorIdNot(sharerId).stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public Collection<ItemRequestDto> getAllUserRequests(long sharerId) {
        if (!users.existsById(sharerId)) {
            log.error("Пользователь с id = {} не найден", sharerId);
            throw new WrongUserException("Пользователь с id = " + sharerId + " не найден");
        }

        Collection<ItemRequestDto> userRequests = requests.findByRequestorId(sharerId).stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();

        userRequests.forEach(request -> request.setItems(items.findByRequestId(request.getId()).stream()
                .map(ItemMapper::mapToItemIdAndNameDto).toList()));
        return userRequests;
    }

    @Override
    public ItemRequestDto getRequest(long requestId) {

        Optional<ItemRequest> optionalItemRequest = requests.findById(requestId);
        if (optionalItemRequest.isEmpty()) {
            log.error("Запрос вещи с id = {} не найден", requestId);
            throw new NotFoundException("Запрос вещи с id = " + requestId + " не найден");
        }

        ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(optionalItemRequest.get());

        itemRequestDto.setItems(items.findByRequestId(requestId).stream()
                .map(ItemMapper::mapToItemIdAndNameDto)
                .toList());

        return itemRequestDto;
    }


}
