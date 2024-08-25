package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private boolean isEmailValid(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }


    @Override
    public UserDto create(User user) {
        log.info("Обновление пользователя  {}", user);
        if (userRepository.isEmailExistInRepository(user.getEmail())) {
            log.error("Email " + user.getEmail() + " уже занят");
            throw new DuplicateEmailException("Email " + user.getEmail() + " уже занят");
        }
        UserDto userDto = UserMapper.mapToUserDto(userRepository.create(user));
        log.info("Создан пользователь  {}", user);
        return userDto;

    }

    @Override
    public UserDto read(long userId) {
        log.info("Чтение пользователя c id =  {}", userId);
        UserDto userDto = UserMapper.mapToUserDto(userRepository.read(userId));
        log.info("Прочитан пользователь c id =  {} {}", userId, userDto);
        return userDto;
    }

    @Override
    public Collection<UserDto> readAll() {
        log.info("Чтение всех пользователей");
        Collection<UserDto> allUsers = userRepository.readAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
        log.info("Прочитаны пользователи {}", allUsers);
        return allUsers;
    }

    @Override
    public UserDto update(long userId, User updUser) {
        updUser.setId(userId);
        log.info("Обновление пользователя id = {} {}", userId, updUser);
        if (updUser.getEmail() != null) {
            if (updUser.getEmail().isEmpty()
                    || !isEmailValid(updUser.getEmail())) {
                log.error("Введен невалидный email");
                throw new InvalidDataException("Введен невалидный email = " + updUser.getEmail());
            }
            if (userRepository.isEmailExistInRepository(updUser.getEmail())) {
                log.error("Email " + updUser.getEmail() + " уже занят");
                throw new DuplicateEmailException("Email " + updUser.getEmail() + " уже занят");
            }
        }

        UserDto userDto = UserMapper.mapToUserDto(userRepository.update(updUser));
        log.info("Обновлен пользователь {}", userDto);
        return userDto;
    }

    @Override
    public void delete(long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        userRepository.delete(userId);
        log.info("Удален пользователь с id ={}", userId);

    }

}
