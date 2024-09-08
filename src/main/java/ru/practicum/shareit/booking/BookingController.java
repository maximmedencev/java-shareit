package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingParamDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long sharerId,
                             @Valid @RequestBody BookingParamDto bookingParamDto) {
        return bookingService.save(sharerId, bookingParamDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable long bookingId,
                              @RequestHeader("X-Sharer-User-Id") long sharerId,
                              @RequestParam boolean approved) {
        return bookingService.setApproved(bookingId, sharerId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto read(@RequestHeader("X-Sharer-User-Id") long sharerId, @PathVariable long bookingId) {
        return bookingService.read(sharerId, bookingId);
    }

    @GetMapping
    public List<BookingDto> readAllByBookerId(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                              @RequestParam(defaultValue = "ALL") BookingsState state) {
        System.out.println(state);
        return bookingService.readAllByBookerId(sharerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> readAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                             @RequestParam(defaultValue = "ALL") BookingsState state) {
        return bookingService.readAllByOwnerId(sharerId, state);
    }

}
