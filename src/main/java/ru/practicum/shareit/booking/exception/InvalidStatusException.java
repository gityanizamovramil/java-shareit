package ru.practicum.shareit.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidStatusException extends Exception {
    public InvalidStatusException(String message) {
        super(message);
    }
}
