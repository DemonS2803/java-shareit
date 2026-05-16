package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@With
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
