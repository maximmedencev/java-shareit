package ru.practicum.shareit.exception;

public class NoItemBookingsForThisUserException extends RuntimeException {
    public NoItemBookingsForThisUserException(String message) {
        super(message);
    }
}
