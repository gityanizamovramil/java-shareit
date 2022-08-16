package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto
    ) throws ValidationException, DuplicateEmailException {
        User user = UserMapper.toUser(userDto);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto)
            throws UserNotFoundException, ValidationException, DuplicateEmailException {
        userDto.setId(userId);
        User repoUser = userRepository.get(userId);
        if (repoUser == null) throw new UserNotFoundException("user not found");
        User user = UserMapper.matchUser(userDto, repoUser);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto get(Long userId) throws UserNotFoundException {
        User repoUser = userRepository.get(userId);
        if (repoUser == null) throw new UserNotFoundException("user not found");
        return UserMapper.toUserDto(repoUser);
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public List<UserDto> get() {
        List<User> users = userRepository.get();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }
}
