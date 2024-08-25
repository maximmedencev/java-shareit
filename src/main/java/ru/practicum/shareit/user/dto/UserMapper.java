package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId() != null ? user.getId() : null)
                .name(user.getName() != null ? user.getName() : null)
                .email(user.getEmail() != null ? user.getEmail() : null)
                .build();
    }

    public static User mapToUser(UserDto userDto) {
        System.out.println(userDto);
        return User.builder()
                .id(userDto.getId() != null ? userDto.getId() : null)
                .name(userDto.getName() != null ? userDto.getName() : null)
                .email(userDto.getEmail() != null ? userDto.getEmail() : null)
                .build();
    }
}
