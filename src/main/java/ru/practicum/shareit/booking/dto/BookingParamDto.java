package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@ToString
public class BookingParamDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private User booker;
    private Item item;
    private BookingStatus status;
}
