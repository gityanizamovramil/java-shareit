package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.user.exception.DuplicateEmailException;
import ru.practicum.shareit.user.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long lastId = 1;

    @Override
    public User save(User user) throws ValidationException, DuplicateEmailException {
        validate(user);
        getId(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User get(Long userId) {
        return users.get(userId);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
    }

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    private void getId(User user) {
        if (user.getId() == null) {
            user.setId(lastId);
            lastId++;
        }
    }

    private void validate(User user) throws ValidationException, DuplicateEmailException {
        if (!StringUtils.hasText(user.getName())) throw new ValidationException("no name");
        if (!StringUtils.hasText(user.getEmail())) throw new ValidationException("no email");
        if (isDuplicate(user)) throw new DuplicateEmailException("invalid email");
    }

    private boolean isDuplicate(User user) {
        final String email = user.getEmail();
        final Long userId = user.getId();
        List<User> duplicateUsers = users.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u -> !u.getId().equals(userId))
                .collect(Collectors.toList());
        return !duplicateUsers.isEmpty();
    }
}
