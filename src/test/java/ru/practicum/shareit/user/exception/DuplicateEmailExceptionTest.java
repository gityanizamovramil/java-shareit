package ru.practicum.shareit.user.exception;

import org.junit.jupiter.api.Test;

class DuplicateEmailExceptionTest {

    @Test
    void setDuplicateEmailException() {
        DuplicateEmailException duplicateEmailException = new DuplicateEmailException("test");
    }


}