package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserDto {

    private Long id;
    @Email(message = "User email must have valid format")
    private String email;
    private String name;
    private String surname;

}
