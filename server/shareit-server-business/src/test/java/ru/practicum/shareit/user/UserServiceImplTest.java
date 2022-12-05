package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    @Autowired
    private final UserServiceImpl userService;

    @MockBean
    private final UserRepository userRepository;

    @Test
    void create() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();

        User user = User.builder()
                .id(1L)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();

        when(userRepository.save(any()))
                .thenReturn(user);

        userDto = userService.create(userDto);
        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void update() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("name updated")
                .email("userUpdated@email.com")
                .build();

        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        when(userRepository.save(any()))
                .thenReturn(user);

        userDto = userService.update(1L, userDto);

        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void throwUserNotFoundException() {
        UserDto userDto = UserDto.builder()
                .name("name updated")
                .email("userUpdated@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        final UserNotFoundException updateException = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.update(1L, userDto)
        );

        final UserNotFoundException getException = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.get(1L)
        );

        assertThat(updateException.getMessage(), is("user not found"));
        assertThat(getException.getMessage(), is("user not found"));
    }


    @Test
    void getUser() throws Exception {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.get(1L);

        assertThat(userDto, is(notNullValue()));
    }

    @Test
    void delete() {

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);

    }

    @Test
    void getAll() {

        List<User> users = List.of(User.builder()
                .id(1L)
                .name("name")
                .email("user@email.com")
                .build());

        when(userRepository.findAll())
                .thenReturn(users);

        List<UserDto> userDtos = userService.get();

        assertThat(userDtos, is(notNullValue()));
    }
}