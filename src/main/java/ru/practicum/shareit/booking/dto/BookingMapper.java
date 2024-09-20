package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemIdAndNameDto;
import ru.practicum.shareit.user.dto.UserIdOnlyDto;

public class BookingMapper {
    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());

        ItemIdAndNameDto itemIdAndNameDto = ItemIdAndNameDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .build();
        bookingDto.setItem(itemIdAndNameDto);

        UserIdOnlyDto booker = UserIdOnlyDto.builder()
                .id(booking.getBooker().getId())
                .build();
        bookingDto.setBooker(booker);

        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static Booking mapToBooking(BookingParamDto bookingParamDto) {
        Booking booking = new Booking();
        booking.setId(bookingParamDto.getId());
        booking.setStart(bookingParamDto.getStart());
        booking.setEnd(bookingParamDto.getEnd());
        booking.setStatus(bookingParamDto.getStatus());
        return booking;
    }
}
