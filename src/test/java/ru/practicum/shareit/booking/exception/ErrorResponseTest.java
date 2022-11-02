package ru.practicum.shareit.booking.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void getError() {
        ErrorResponse response = new ErrorResponse("test");
        assertEquals("test", response.getError());
    }
}