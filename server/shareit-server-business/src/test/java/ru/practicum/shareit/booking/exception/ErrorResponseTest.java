package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

    @Test
    void getError() {
        ErrorResponse response = new ErrorResponse("test");
        assertEquals("test", response.getError());
    }
}