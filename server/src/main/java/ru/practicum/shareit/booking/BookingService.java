package ru.practicum.shareit.booking;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingParamDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    @Transactional
    BookingDto save(Long sharerId, BookingParamDto bookingParamDto);

    @Transactional
    BookingDto setApproved(Long bookingId, Long sharerId, Boolean approved);

    BookingDto read(Long sharerId, Long bookingId);

    List<BookingDto> readAllByBookerId(Long sharerId, BookingsState state);

    List<BookingDto> readAllByOwnerId(Long sharerId, BookingsState state);

}
