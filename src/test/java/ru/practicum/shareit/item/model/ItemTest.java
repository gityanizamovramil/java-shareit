package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void testEquals() {
        Item item1 = Item.builder()
                .id(1L)
                .build();

        Item item2 = Item.builder()
                .id(1L)
                .build();

        Item item3 = Item.builder()
                .id(null)
                .build();

        Assertions.assertTrue(item1.equals(item2));
        Assertions.assertTrue(item1.equals(item1));
        Assertions.assertFalse(item1.equals(1L));
        Assertions.assertFalse(item1.equals(item3));
        Assertions.assertFalse(item3.equals(item1));
    }

    @Test
    void testHashCode() {
        Item item1 = Item.builder()
                .id(1L)
                .build();

        Assertions.assertEquals(1, item1.hashCode());
    }
}