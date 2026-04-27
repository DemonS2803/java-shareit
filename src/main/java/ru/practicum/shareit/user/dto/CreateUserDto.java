package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateUserDto {

    @Email
    @NotNull(message = "User must have valid email")
    private String email;
    @NotNull
    private String name;
    private String surname;

}
