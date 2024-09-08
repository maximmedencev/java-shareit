package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NoAvailableFieldException;
import ru.practicum.shareit.exception.NoItemBookingsForThisUserException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentParamDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository items;
    private final UserRepository users;
    private final BookingRepository bookings;
    private final CommentRepository comments;

    @Override
    public ItemDto create(long sharerId, Item item) {
        log.info("Добавляем вещь {}", item);
        if (item.getAvailable() == null) {
            log.error("Отстутсвует поле \"available\"");
            throw new NoAvailableFieldException("Отстутсвует поле \"available\"");
        }

        Optional<User> optionalUser = users.findById(sharerId);
        if (optionalUser.isEmpty()) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        item.setOwner(optionalUser.get());
        ItemDto itemDto = ItemMapper.mapToItemDto(items.save(item));
        log.info("Создана вещь {}", itemDto);

        return itemDto;
    }

    @Override
    public ItemDto read(long itemId) {
        log.info("Чтение вещи с id = {}", itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(items.findById(itemId).orElse(new Item()));
        log.info("Прочитана вещь с id = {}, {}", itemId, itemDto);
        return itemDto;
    }

    @Override
    public Collection<ItemDto> readAll(long sharerId) {
        log.info("Чтение вещей пользователя c id = {}", sharerId);

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        Collection<ItemDto> sharerItems = items.findByOwnerId(sharerId).stream()
                .map(ItemMapper::mapToItemDto)
                .peek(itemDto -> itemDto.setLastBooking(bookings.findLastBookingEndDate(itemDto.getId(), now)))
                .peek(itemDto -> itemDto.setNextBooking(bookings.findNextBookingStartDate(itemDto.getId(), now)))
                .toList();
        log.info("Чтение вещей пользователя c id = {} {}", sharerId, sharerItems);
        return sharerItems;
    }

    @Override
    public ItemDto update(long itemId, long sharerId, Item item) {
        log.info("Обновление вещи c id = {} пользователя с id = {}", itemId, sharerId);
        if (!isUserValid(sharerId, itemId)) {
            log.error("Эта вещь не принадлежит пользователю с id = " + sharerId);
            throw new NotFoundException("Эта вещь не принадлежит пользователю с id = " + sharerId);
        }

        Optional<Item> optionalItem = items.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Вещь с id = " + itemId + " не найдена");
        }
        Item updatedItem = updateItemFieldsForPatch(optionalItem.get(), item);

        ItemDto itemDto = ItemMapper.mapToItemDto(items.save(updatedItem));
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
        items.deleteById(itemId);
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

    @Override
    public CommentDto createComment(long sharerId, long itemId, CommentParamDto commentParamDto) {
        log.info("Добавляем комментарий {}", commentParamDto);

        if (!bookings.isUserBookedTheItem(itemId, sharerId, Timestamp.valueOf(LocalDateTime.now()))) {
            throw new NoItemBookingsForThisUserException("Пользователь с id = " + sharerId
                    + " не бронировал вещь с id = " + itemId);
        }

        Comment comment = new Comment();
        comment.setText(commentParamDto.getText());
        Optional<User> optionalUser = users.findById(sharerId);
        if (optionalUser.isEmpty()) {
            log.error("Пользователь с id" + sharerId + " не найден");
            throw new NotFoundException("Пользователь c id " + sharerId + " не найден");
        }

        Optional<Item> optionalItem = items.findById(itemId);
        if (optionalItem.isEmpty()) {
            log.error("Вещь не найдена");
            throw new NotFoundException("Вещь c id " + itemId + " не найдена");
        }

        comment.setItem(optionalItem.get());
        comment.setAuthor(optionalUser.get());
        comment.setCreated(LocalDateTime.now());
        CommentDto commentDto = CommentMapper.mapToCommentDto(comments.save(comment));
        log.info("Создан комментарий {}", comment);

        return commentDto;
    }

    private boolean isUserValid(long sharerId, long itemId) {
        return sharerId == items.findById(itemId).get()
                .getOwner()
                .getId();
    }

    private Item updateItemFieldsForPatch(Item oldItem, Item newItem) {
        Item item = new Item();
        item.setName(newItem.getName() == null ? oldItem.getName() : newItem.getName());
        item.setDescription(newItem.getDescription() == null ? oldItem.getDescription() : newItem.getDescription());
        item.setAvailable(newItem.getAvailable() == null ? oldItem.getAvailable() : newItem.getAvailable());
        return item;
    }

}
