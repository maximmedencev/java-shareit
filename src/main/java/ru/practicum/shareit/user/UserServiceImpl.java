package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Transactional
    @Override
    public UserDto create(User user) {
        log.info("Обновление пользователя  {}", user);

        if (userRepository.existsByEmail(user.getEmail())) {
            log.error("Email " + user.getEmail() + " уже занят");
            throw new DuplicateEmailException("Email " + user.getEmail() + " уже занят");
        }

        UserDto userDto = UserMapper.mapToUserDto(userRepository.save(user));
        log.info("Создан пользователь  {}", user);
        return userDto;

    }

    @Override
    public UserDto read(long userId) {
        log.info("Чтение пользователя c id =  {}", userId);
        UserDto userDto = UserMapper.mapToUserDto(userRepository
                .findById(userId)
                .orElse(new User()));
        log.info("Прочитан пользователь c id =  {} {}", userId, userDto);
        return userDto;
    }

    @Override
    public Collection<UserDto> readAll() {
        log.info("Чтение всех пользователей");
        Collection<UserDto> allUsers = userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
        log.info("Прочитаны пользователи {}", allUsers);
        return allUsers;
    }

    @Transactional
    @Override
    public UserDto update(long userId, User updUser) {
        updUser.setId(userId);

        UserDto userDto = UserMapper.mapToUserDto(updUser);

        log.info("Обновление пользователя id = {} {}", userId, updUser);
        if (updUser.getEmail() != null) {
            if (updUser.getEmail().isEmpty()
                    || !isEmailValid(updUser.getEmail())) {
                log.error("Введен невалидный email");
                throw new InvalidDataException("Введен невалидный email = " + updUser.getEmail());
            }
            if (userRepository.existsByEmail(updUser.getEmail())) {
                log.error("Email " + updUser.getEmail() + " уже занят");
                throw new DuplicateEmailException("Email " + updUser.getEmail() + " уже занят");
            }
        } else {
            userRepository.updateName(userId, updUser.getName());
            log.info("Обновлен пользователь {}", userDto);
            return userDto;
        }

        if (updUser.getName() == null && updUser.getEmail() != null) {
            userRepository.updateEmail(userId, updUser.getEmail());
            log.info("Обновлен пользователь {}", userDto);
            return userDto;
        }

        userDto = UserMapper.mapToUserDto(userRepository.save(updUser));
        log.info("Обновлен пользователь {}", userDto);
        return userDto;
    }

    @Override
    public void delete(long userId) {
        log.info("Удаление пользователя с id = {}", userId);
        userRepository.deleteById(userId);
        log.info("Удален пользователь с id ={}", userId);

    }

    private boolean isEmailValid(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }

}
