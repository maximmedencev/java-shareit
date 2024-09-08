package ru.practicum.shareit.exception;

public class NoItemBookingsForThisUser extends RuntimeException {
    public NoItemBookingsForThisUserException(String message) {
        super(message);
    }
}
