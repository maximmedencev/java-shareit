package ru.practicum.shareit.exception;

public class BookingApproveByWrongUserException extends RuntimeException {
    public BookingApproveByWrongUserException(String message) {
        super(message);
    }
}
