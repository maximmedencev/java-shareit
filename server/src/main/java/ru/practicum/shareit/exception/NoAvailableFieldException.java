package ru.practicum.shareit.exception;

public class NoAvailableFieldException extends RuntimeException {
    public NoAvailableFieldException(String message) {
        super(message);
    }
}