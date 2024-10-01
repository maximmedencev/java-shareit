package ru.practicum.shareit.exception;

public class BookingUnavailableItemException extends RuntimeException {
    public BookingUnavailableItemException(String message) {
        super(message);
    }
}

