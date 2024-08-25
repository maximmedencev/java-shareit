package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepository {
    User create(User user);

    User read(long userId);

    Collection<User> readAll();

    User update(User updUser);

    void delete(long userId);

    boolean isEmailExistInRepository(String email);

    boolean isUserExist(long id);
}
