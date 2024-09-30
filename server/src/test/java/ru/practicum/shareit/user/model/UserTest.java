package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.util.Objects;

public class UserTest {

    @DisplayName("Должен быть равным объекту с таким же id")
    @Test
    void shouldEqual() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1 name");

        User user2 = new User();
        user2.setId(1L);
        user2.setName("user1 name");
        Assertions.assertTrue(user1.equals(user2));

    }

    @DisplayName("Не должен быть равным объекту с другим id")
    @Test
    void shouldNotEqual() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1 name");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("user1 name");
        Assertions.assertFalse(user1.equals(user2));

    }

    @DisplayName("Должен возвращать корректный хэшкод")
    @Test
    void shouldReturnCorrectHashcode() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("user1 name");
        Assertions.assertEquals(user1.hashCode(), Objects.hashCode(user1));

    }
}
