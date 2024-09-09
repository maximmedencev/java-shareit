package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse errorHandler(final Throwable e) {
        return new ErrorResponse("error", "Произошла непредвиденная ошибка");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            NoItemBookingsForThisUserException.class,
            BookingUnavailableItemException.class,
            BookingUnavailableItemException.class,
            BookingApproveByWrongUserException.class,
            NoAvailableFieldException.class,
            InvalidDataException.class
    })
    public ErrorResponse handleInvalidData(final RuntimeException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResponse handleWrongUser(final WrongUserException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse duplicateEmail(final DuplicateEmailException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFound(final NotFoundException e) {
        return new ErrorResponse("error", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleNotValidData(final MethodArgumentNotValidException e) {

        String errorMessage = "Запрос содержит невалидные данные: поле "
                + e.getFieldError().getField()
                + " "
                + e.getFieldError().getDefaultMessage();

        return new ErrorResponse("error", errorMessage);
    }

    @Getter
    private static class ErrorResponse {
        String error;
        String description;

        public ErrorResponse(String error, String description) {
            this.error = error;
            this.description = description;
        }
    }
}
