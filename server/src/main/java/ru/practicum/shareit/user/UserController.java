package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody User user) {
        return userService.create(user);
    }

    @GetMapping("/{userId}")
    public UserDto read(@PathVariable long userId) {
        return userService.read(userId);
    }

    @GetMapping
    public Collection<UserDto> readAll() {
        return userService.readAll();
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @RequestBody User updUser) {
        return userService.update(userId, updUser);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }
}
