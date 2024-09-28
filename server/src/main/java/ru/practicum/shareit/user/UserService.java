package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(User user);

    UserDto read(long userId);

    Collection<UserDto> readAll();

    UserDto update(long userId, User updUser);

    void delete(long userId);
}
