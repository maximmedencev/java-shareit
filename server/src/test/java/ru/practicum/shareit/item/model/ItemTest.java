package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class ItemTest {

    @DisplayName("Должен быть равным объекту с таким же id")
    @Test
    void shouldEqual(){
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1 name");

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("item1 name");
        Assertions.assertTrue(item1.equals(item2));

    }

    @DisplayName("Не должен быть равным объекту с другим id")
    @Test
    void shouldNotEqual(){
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1 name");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("item1 name");
        Assertions.assertFalse(item1.equals(item2));

    }

    @DisplayName("Должен возвращать корректный хэшкод")
    @Test
    void shouldReturnCorrectHashcode(){
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1 name");
        Assertions.assertEquals(item1.hashCode(), Objects.hashCode(item1));

    }
}
