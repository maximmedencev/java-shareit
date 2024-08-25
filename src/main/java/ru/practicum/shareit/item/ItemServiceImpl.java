package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoAvailableFieldException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository items;
    private final UserRepository users;

    @Override
    public ItemDto create(long sharerId, Item item) {
        log.info("Добавляем вещь {}", item);
        if (item.getAvailable() == null) {
            log.error("Отстутсвует поле \"available\"");
            throw new NoAvailableFieldException("Отстутсвует поле \"available\"");
        }
        if (!users.isUserExist(sharerId)) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        item.setOwner(users.read(sharerId));
        ItemDto itemDto = ItemMapper.mapToItemDto(items.create(item));
        log.info("Создана вещь {}", itemDto);

        return itemDto;
    }

    @Override
    public ItemDto read(long itemId) {
        log.info("Чтение вещи с id = {}", itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(items.read(itemId));
        log.info("Прочитана вещь с id = {}, {}", itemId, itemDto);
        return itemDto;
    }


    @Override
    public Collection<ItemDto> readAll(long sharerId) {
        log.info("Чтение вещей пользователя c id = {}", sharerId);
        Collection<ItemDto> sharerItems = items.readAll(sharerId).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
        log.info("Чтение вещей пользователя c id = {} {}", sharerId, sharerItems);
        return sharerItems;
    }

    @Override
    public ItemDto update(long itemId, long sharerId, Item item) {
        log.info("Обновление вещи c id = {} пользователя с id = {}", itemId, sharerId);
        item.setId(itemId);
        if (!isUserValid(sharerId, item.getId())) {
            log.error("Эта вещь не принадлежит пользователю с id = " + sharerId);
            throw new NotFoundException("Эта вещь не принадлежит пользователю с id = " + sharerId);
        }
        ItemDto itemDto = ItemMapper.mapToItemDto(items.update(item));
        log.info("Обновление вещи c id = {} пользователя с id = {} {}", itemId, sharerId, itemDto);
        return itemDto;
    }

    @Override
    public void delete(long sharerId, long itemId) {
        log.info("Удаление вещи c id = {} пользователя с id = {}", itemId, sharerId);
        if (!isUserValid(sharerId, itemId)) {
            log.error("Эта вещь не принадлежит пользователю с id = " + sharerId);
            throw new NotFoundException("Эта вещь не принадлежит пользователю с id = " + sharerId);
        }
        items.delete(itemId);
    }

    @Override
    public Collection<ItemDto> search(long sharerId, String text) {
        log.info("Поиск вещи по строке {}", text);
        if (text.isEmpty()) {
            log.info("Задана пустая поисковая строка");
            return new ArrayList<>();
        }
        Collection<ItemDto> searchResult = items.search(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
        log.info("Результаты поиска вещи по строке {} {}", text, searchResult);
        return searchResult;
    }

    private boolean isUserValid(long sharerId, long itemId) {
        return sharerId == items.getOwnerId(itemId);
    }

}
