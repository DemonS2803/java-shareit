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
public class UpdateUserDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void validateUpdateUserDto_validUpdateUserDto_success() {
        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(VALID_UPDATE_USER_DTO_1);
        log.info("{}", violations);
        assertTrue(violations.isEmpty(), "All fields are valid");
    }

    @Test
    public void validateUpdateUserDto_idNull_shouldSucceed() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(null);
        dto.setEmail(VALID_EMAIL_1);
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Id can be null (but will be validated in service layer)");
    }

    @Test
    public void validateUpdateUserDto_allFieldsNull_shouldSucceed() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(null);
        dto.setEmail(null);
        dto.setName(null);
        dto.setSurname(null);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "All fields can be null for partial update");
    }

    @Test
    public void validateUpdateUserDto_validEmail_shouldSucceed() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(VALID_USER_ID_1);
        dto.setEmail("user@domain.com");
        dto.setName(null);
        dto.setSurname(null);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid email format should pass");
    }

    @Test
    public void validateUpdateUserDto_invalidEmailFormat_shouldFail() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(VALID_USER_ID_1);
        dto.setEmail("invalid-email");
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Invalid email format should fail validation");

        // Verify it's an email constraint violation
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("email"));
    }

    @Test
    public void validateUpdateUserDto_blankEmail_shouldSucceed() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(VALID_USER_ID_1);
        dto.setEmail("");  // Blank email
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        // @Email on a blank string typically passes validation (no @NotBlank)
        // But your service layer will check !isBlank()
        assertTrue(violations.isEmpty(), "Blank email passes validation but won't be updated");
    }

    @Test
    public void validateUpdateUserDto_nullEmail_shouldSucceed() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(VALID_USER_ID_1);
        dto.setEmail(null);
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Null email is allowed (no update)");
    }

    @Test
    public void validateUpdateUserDto_blankName_shouldSucceed() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(VALID_USER_ID_1);
        dto.setEmail(VALID_EMAIL_1);
        dto.setName("   ");  // Only whitespace
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        // Name has no validation annotations, blank passes validation
        // But service layer checks !isBlank() so won't be updated
        assertTrue(violations.isEmpty(), "Blank name passes validation but won't be updated");
    }

    @Test
    public void validateUpdateUserDto_blankSurname_shouldSucceed() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(VALID_USER_ID_1);
        dto.setEmail(VALID_EMAIL_1);
        dto.setName(VALID_NAME_1);
        dto.setSurname("");  // Empty surname

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Blank surname passes validation but won't be updated");
    }

    @Test
    public void validateUpdateUserDto_emailWithWhitespace_shouldFail() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(VALID_USER_ID_1);
        dto.setEmail(" john@example.com ");  // Email with spaces
        dto.setName(VALID_NAME_1);
        dto.setSurname(VALID_SURNAME_1);

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        // @Email validation typically fails on strings with spaces
        assertFalse(violations.isEmpty(), "Email with spaces should fail validation");
    }

    @Test
    public void validateUpdateUserDto_multipleValidFields_success() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setId(VALID_USER_ID_1);
        dto.setEmail("updated.email@example.com");
        dto.setName("Updated Name");
        dto.setSurname("Updated Surname");

        Set<ConstraintViolation<UpdateUserDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "All valid fields should pass");
    }
}
