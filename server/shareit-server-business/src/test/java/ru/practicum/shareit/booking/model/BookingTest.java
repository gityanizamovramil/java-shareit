package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BookingTest {

    @Test
    void testEquals() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .build();

        Booking booking2 = Booking.builder()
                .id(1L)
                .build();

        Booking booking3 = Booking.builder()
                .id(null)
                .build();

        Assertions.assertTrue(booking1.equals(booking2));
        Assertions.assertTrue(booking1.equals(booking1));
        Assertions.assertFalse(booking1.equals(1L));
        Assertions.assertFalse(booking1.equals(booking3));
        Assertions.assertFalse(booking3.equals(booking1));
    }

    @Test
    void testHashCode() {
        Booking booking1 = Booking.builder()
                .id(1L)
                .build();

        Assertions.assertEquals(1, booking1.hashCode());
    }
}