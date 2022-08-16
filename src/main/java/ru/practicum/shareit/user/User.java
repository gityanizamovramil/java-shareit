package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@Builder
public class User {

    private Long id;

    @Pattern(regexp = "\\S*$")
    private String name;

    @Email
    private String email;
}
