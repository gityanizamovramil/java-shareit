package ru.practicum.shareit.user;

import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.List;

public interface UserInMemoryRepository {

    User save(User user) throws ValidationException, DuplicateEmailException;

    User get(Long userId);

    void delete(Long userId);

    List<User> get();
}
