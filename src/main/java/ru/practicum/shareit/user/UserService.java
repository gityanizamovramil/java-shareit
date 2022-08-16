package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto) throws ValidationException, DuplicateEmailException;

    UserDto update(Long userId, UserDto userDto) throws UserNotFoundException, ValidationException, DuplicateEmailException;

    UserDto get(Long userId) throws UserNotFoundException;

    void delete(Long userId);

    List<UserDto> get();
}
