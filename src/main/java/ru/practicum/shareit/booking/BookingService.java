package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.exception.BookingApproveByWrongUserException;
import ru.practicum.shareit.exception.BookingUnavailableItemException;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongUserException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookings;
    private final ItemRepository items;
    private final UserRepository users;


    public BookingDto save(Long sharerId, BookingParamDto bookingParamDto) {

        log.info("Начало создания бронирования {} пользователем c id {}", bookingParamDto, sharerId);
        Optional<User> optionalBooker = users.findById(sharerId);
        if (optionalBooker.isEmpty()) {
            log.error("Пользователь с id = {} не найден", sharerId);
            throw new NotFoundException("Пользователь с id = " + sharerId + " не найден");
        }

        if (bookingParamDto.getStart() == null) {
            log.error("Дата начала бронирования {} равна null", bookingParamDto);
            throw new InvalidDataException(". Дата начала бронирования не может быть равной null.");
        }

        if (bookingParamDto.getEnd() == null) {
            log.error("Дата конца бронирования {} равна null", bookingParamDto);
            throw new InvalidDataException(". Дата конца бронирования не может быть равной null.");
        }

        if (bookingParamDto.getStart().equals(bookingParamDto.getEnd())) {
            log.error("Дата начала бронирования такая же, как дата конца {}", bookingParamDto);
            throw new InvalidDataException(". Дата начала бронирования такая же, как дата конца.");
        }

        if (bookingParamDto.getStart().isBefore(LocalDateTime.now())) {
            log.error("Дата конца бронирования не может быть в прошлом {}", bookingParamDto);
            throw new InvalidDataException(". Дата конца бронирования не может быть в прошлом.");
        }

        Booking booking = BookingMapper.mapToBooking(bookingParamDto);

        Item item = getItem(bookingParamDto.getItemId());

        if (!item.getAvailable()) {
            log.error("Вещь с id = {} недоступна для бронирования", item.getId());
            throw new BookingUnavailableItemException("Вещь с id = " + item.getId() + " недоступна для бронирования");
        }

        booking.setItem(item);

        User booker = optionalBooker.get();

        booking.setBooker(booker);

        booking.setStatus(BookingStatus.WAITING);
        log.error("Создано бронирование {}", booking);
        return BookingMapper.mapToBookingDto(bookings.save(booking));
    }

    public BookingDto setApproved(Long bookingId, Long sharerId, Boolean approved) {
        log.info("Начало установки статуса approved = {} бронированию с id = {} пользователем с id = {}",
                approved, bookingId, sharerId);
        if (!users.existsById(sharerId)) {
            log.error("Пользователь с id = {} не найден", sharerId);
            throw new WrongUserException("Пользователь с id = " + sharerId + " не найден");
        }

        Optional<Booking> optionalBooking = bookings.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            log.error("Бронирование с id = {} не найдено", bookingId);
            throw new NotFoundException("Бронирование с id = " + bookingId + " не найдено");
        }

        Booking booking = optionalBooking.get();

        BookingStatus bookingStatus = BookingStatus.APPROVED;
        if (!approved) {
            bookingStatus = BookingStatus.REJECTED;
        }

        bookings.updateBookingStatus(bookingId, bookingStatus);

        Optional<Item> optionalItem = items.findById(booking.getItem().getId());
        if (optionalItem.isEmpty()) {
            log.error("Вещь с id = {} не найдена", booking.getItem().getId());
            throw new NotFoundException("Вещь с id = " + booking.getItem().getId() + " не найдена");
        }

        Item item = optionalItem.get();

        if (!Objects.equals(item.getOwner().getId(), sharerId)) {
            log.error("Попытка подтвердить бронирование вещи с id = {} не владельцем(id = {})",
                    booking.getItem().getId(),
                    sharerId);
            throw new BookingApproveByWrongUserException("Попытка подтвердить бронирование не владельцем");
        }
        booking.setItem(item);

        booking.setStatus(BookingStatus.APPROVED);
        log.info("Установлен статус бронированию {}", booking);
        return BookingMapper.mapToBookingDto(bookings.save(booking));
    }

    public BookingDto read(Long sharerId, Long bookingId) {
        log.info("Чтение данных бронирования с id = {} пользователем с id = {}", bookingId, sharerId);
        if (!users.existsById(sharerId)) {
            log.error("Пользователь с id = {} не найден", sharerId);
            throw new NotFoundException("Пользователь с id = " + sharerId + " не найден");
        }
        Optional<Booking> optionalBooking = bookings.findById(bookingId);

        if (optionalBooking.isEmpty()) {
            log.error("Бронирование с id = {} не найдено", bookingId);
            throw new NotFoundException("Бронирование с id = " + bookingId + " не найдено");
        }

        log.info("Прочитано бронирование {}", optionalBooking.get());
        return BookingMapper.mapToBookingDto(optionalBooking.get());
    }

    public List<BookingDto> readAllByBookerId(Long sharerId, BookingsState state) {
        log.info("Чтение всех бронирований букера с id = {}", sharerId);
        if (!users.existsById(sharerId)) {
            log.error("Пользователь с id = {} не найден", sharerId);
            throw new WrongUserException("Пользователь с id = " + sharerId + " не найден");
        }

        return switch (state) {
            case ALL -> bookings.findByBookerId(sharerId, Sort.by("end").descending()).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case PAST -> bookings.findByBookerIdAndEndIsBefore(sharerId,
                            LocalDateTime.now(),
                            Sort.by("end").descending()).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case FUTURE -> bookings.findByBookerIdAndStartIsAfter(sharerId,
                            LocalDateTime.now(),
                            Sort.by("end")).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case CURRENT -> bookings.findByBookerIdAndStartIsBeforeAndEndIsAfter(sharerId,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            Sort.by("end")).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case WAITING -> bookings.findAllByBookerIdAndStatus(sharerId,
                            BookingStatus.WAITING,
                            Sort.by("end").descending()).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case REJECTED -> bookings.findAllByBookerIdAndStatus(sharerId,
                            BookingStatus.REJECTED,
                            Sort.by("end").descending()).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
        };

    }

    public List<BookingDto> readAllByOwnerId(Long sharerId, BookingsState state) {
        log.info("Чтение всех бронирований пользователя с id = {}", sharerId);
        if (!users.existsById(sharerId)) {
            log.error("Пользователь с id = {} не найден", sharerId);
            throw new WrongUserException("Пользователь с id = " + sharerId + " не найден");
        }
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());

        return switch (state) {
            case ALL -> bookings.findAllOwnerItemsBookings(sharerId).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case PAST -> bookings.findAllOwnerItemsBookingsInPast(sharerId, now).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case FUTURE -> bookings.findAllOwnerItemsBookingsInFuture(sharerId, now).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case CURRENT -> bookings.findAllCurrentOwnerItemsBooking(sharerId, now).stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case WAITING -> bookings.findAllOwnerItemsBookingWithStatus(sharerId, BookingStatus.WAITING.toString())
                    .stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
            case REJECTED -> bookings.findAllOwnerItemsBookingWithStatus(sharerId, BookingStatus.REJECTED.toString())
                    .stream()
                    .map(BookingMapper::mapToBookingDto)
                    .toList();
        };

    }

    private Item getItem(Long id) {
        Optional<Item> optionalItem = items.findById(id);
        if (optionalItem.isEmpty()) {
            log.error("Вещь с id = {}", id);
            throw new NotFoundException("Вещь с id = " + id + " не найдена");
        }
        return optionalItem.get();
    }

}
