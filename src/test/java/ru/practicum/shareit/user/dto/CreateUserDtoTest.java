package ru.practicum.shareit.user.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static ru.practicum.shareit.common.testutil.TestStubs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class CreateUserDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void validateCreateUserDto_validCreateUserDto_success() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail(VALID_EMAIL_1);
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "All fields are valid");
    }

    @Test
    public void validateCreateUserDto_emailNull_shouldFail() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail(null);
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Email must not be null");

        assertEquals(1, violations.size());
        assertEquals("User must have valid email", violations.iterator().next().getMessage());
    }

    @Test
    public void validateCreateUserDto_emailBlank_shouldFail() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("");
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Email must not be blank");
    }

    @Test
    public void validateCreateUserDto_emailInvalidFormat_shouldFail() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("invalid-email");
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Email must have valid format");
    }

    @Test
    public void validateCreateUserDto_nameNull_shouldFail() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail(VALID_EMAIL_1);
        dto.setName(null);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Name must not be null");

        assertEquals(1, violations.size());
        assertEquals("User must have not empty name", violations.iterator().next().getMessage());
    }

    @Test
    public void validateCreateUserDto_nameBlank_shouldFail() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail(VALID_EMAIL_1);
        dto.setName("");
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Name must not be blank");
    }

    @Test
    public void validateCreateUserDto_surnameNull_shouldSucceed() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail(VALID_EMAIL_1);
        dto.setName(VALID_NAME_1);
        dto.setSurname(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Surname can be null since it has no constraints");
    }

    @Test
    public void validateCreateUserDto_surnameBlank_shouldSucceed() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail(VALID_EMAIL_1);
        dto.setName(VALID_NAME_1);
        dto.setSurname("");

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Surname can be empty since it has no constraints");
    }

    @Test
    public void validateCreateUserDto_multipleInvalidFields_shouldShowAllViolations() {
        CreateUserDto dto = new CreateUserDto();
        dto.setEmail("invalid");
        dto.setName("");
        dto.setSurname(null);

        Set<ConstraintViolation<CreateUserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have multiple violations");
        assertEquals(2, violations.size(), "Should have 2 constraint violations (email and name)");
    }
}
