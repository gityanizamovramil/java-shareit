package ru.practicum.shareit.user.exception;

import org.junit.jupiter.api.Test;

class ValidationExceptionTest {

    @Test
    void setValidationException() {
        ValidationException validationException = new ValidationException("test");
    }

}