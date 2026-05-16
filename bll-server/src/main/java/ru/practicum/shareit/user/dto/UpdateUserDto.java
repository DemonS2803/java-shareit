package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    private Long id;
    @Email(message = "User email must have valid format")
    private String email;
    private String name;
    private String surname;

}
