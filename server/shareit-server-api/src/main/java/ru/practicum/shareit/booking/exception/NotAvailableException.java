package ru.practicum.shareit.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotAvailableException extends Exception {
    public NotAvailableException(String message) {
        super(message);
    }
}
