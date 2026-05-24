package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateUserDto {

    @Email
    @NotBlank(message = "User must have valid email")
    private String email;
    @NotBlank(message = "User must have not empty name")
    private String name;
    private String surname;

}
