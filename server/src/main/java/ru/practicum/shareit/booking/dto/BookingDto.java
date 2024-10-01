package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemIdAndNameDto;
import ru.practicum.shareit.user.dto.UserIdOnlyDto;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private UserIdOnlyDto booker;
    private ItemIdAndNameDto item;
    private BookingStatus status;
}
