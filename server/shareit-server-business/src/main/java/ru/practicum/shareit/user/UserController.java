package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) throws ValidationException, DuplicateEmailException {
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId,
                          @RequestBody UserDto userDto
    ) throws UserNotFoundException, ValidationException, DuplicateEmailException {
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) throws UserNotFoundException {
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> get() {
        return userService.get();
    }
}
