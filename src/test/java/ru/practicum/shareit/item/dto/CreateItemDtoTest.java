package ru.practicum.shareit.item.dto;

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
public class CreateItemDtoTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void validateCreateItemDto_validCreateItemDto_success() {
        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(VALID_CREATE_ITEM_DTO);
        log.info("{}", violations);
        assertTrue(violations.isEmpty(), "All fields are valid");
    }

    @Test
    public void validateCreateItemDto_nameNull_shouldFail() {
        CreateItemDto dto = new CreateItemDto(
                null,
                VALID_ITEM_DESCRIPTION_1,
                ITEM_AVAILABLE
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Name must not be null");

        assertEquals(1, violations.size());
        assertEquals("Item name mustn't be empty", violations.iterator().next().getMessage());
    }

    @Test
    public void validateCreateItemDto_nameBlank_shouldFail() {
        CreateItemDto dto = new CreateItemDto(
                "",
                VALID_ITEM_DESCRIPTION_1,
                ITEM_AVAILABLE
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Name must not be blank");

        assertEquals(1, violations.size());
        assertEquals("Item name mustn't be empty", violations.iterator().next().getMessage());
    }

    @Test
    public void validateCreateItemDto_nameWithWhitespaceOnly_shouldFail() {
        CreateItemDto dto = new CreateItemDto(
                "   ",
                VALID_ITEM_DESCRIPTION_1,
                ITEM_AVAILABLE
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Name with only whitespace should fail");
    }

    @Test
    public void validateCreateItemDto_descriptionNull_shouldFail() {
        CreateItemDto dto = new CreateItemDto(
                VALID_ITEM_NAME_1,
                null,
                ITEM_AVAILABLE
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Description must not be null");

        assertEquals(1, violations.size());
        assertEquals("Item description mustn't be empty", violations.iterator().next().getMessage());
    }

    @Test
    public void validateCreateItemDto_descriptionBlank_shouldFail() {
        CreateItemDto dto = new CreateItemDto(
                VALID_ITEM_NAME_1,
                "",
                ITEM_AVAILABLE
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Description must not be blank");

        assertEquals(1, violations.size());
        assertEquals("Item description mustn't be empty", violations.iterator().next().getMessage());
    }

    @Test
    public void validateCreateItemDto_descriptionWithWhitespaceOnly_shouldFail() {
        CreateItemDto dto = new CreateItemDto(
                VALID_ITEM_NAME_1,
                "   ",
                ITEM_AVAILABLE
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Description with only whitespace should fail");
    }

    @Test
    public void validateCreateItemDto_availableNull_shouldFail() {
        CreateItemDto dto = new CreateItemDto(
                VALID_ITEM_NAME_1,
                VALID_ITEM_DESCRIPTION_1,
                null
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Available must not be null");

        assertEquals(1, violations.size());
        assertEquals("New item must have 'available' property", violations.iterator().next().getMessage());
    }

    @Test
    public void validateCreateItemDto_availableTrue_success() {
        CreateItemDto dto = new CreateItemDto(
                VALID_ITEM_NAME_1,
                VALID_ITEM_DESCRIPTION_1,
                true
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Available = true is valid");
    }

    @Test
    public void validateCreateItemDto_availableFalse_success() {
        CreateItemDto dto = new CreateItemDto(
                VALID_ITEM_NAME_1,
                VALID_ITEM_DESCRIPTION_1,
                false
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Available = false is valid");
    }

    @Test
    public void validateCreateItemDto_multipleInvalidFields_shouldShowAllViolations() {
        CreateItemDto dto = new CreateItemDto(
                "",
                null,
                null
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Should have multiple violations");
        assertEquals(3, violations.size(), "Should have 3 constraint violations (name, description, available)");
    }

    @Test
    public void validateCreateItemDto_nameAndDescriptionWithExtraWhitespace_shouldBeTrimmed() {
        // Note: @NotBlank trims whitespace before validation
        CreateItemDto dto = new CreateItemDto(
                "  Laptop  ",
                "  Gaming laptop  ",
                true
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        // @NotBlank considers non-whitespace characters after trimming
        assertTrue(violations.isEmpty(), "Whitespace around valid text is allowed");
    }

    @Test
    public void validateCreateItemDto_nameVeryLong_shouldPass() {
        CreateItemDto dto = new CreateItemDto(
                VERY_LONG_STRING,
                VALID_ITEM_DESCRIPTION_1,
                ITEM_AVAILABLE
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        // No @Size annotation, so any length passes
        assertTrue(violations.isEmpty(), "Very long name is allowed (no size constraint)");
    }

    @Test
    public void validateCreateItemDto_specialCharactersInName_shouldPass() {
        CreateItemDto dto = new CreateItemDto(
                "Laptop Pro 15\" - Model #XYZ",
                VALID_ITEM_DESCRIPTION_1,
                ITEM_AVAILABLE
        );

        Set<ConstraintViolation<CreateItemDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Special characters in name are allowed");
    }
}
