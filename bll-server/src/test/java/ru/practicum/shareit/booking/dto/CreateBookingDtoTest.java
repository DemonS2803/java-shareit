package ru.practicum.shareit.booking.dto;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static ru.practicum.shareit.common.testutil.TestStubs.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class CreateBookingDtoTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void validateCreateBookingDto_validCreateBookingDto_success() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(VALID_ITEM_ID_1);
        dto.setStart(VALID_AFTER_1_HOUR_LDT);
        dto.setEnd(VALID_AFTER_1_DAY_LDT);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "That was valid film!");
    }

    @Test
    public void validateCreateBookingDto_startDateNull_shouldFail() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(VALID_ITEM_ID_1);
        dto.setStart(null);
        dto.setEnd(VALID_AFTER_1_DAY_LDT);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "CreateBookingDto mustn't have start null");
    }

    @Test
    public void validateCreateBookingDto_endDateNull_shouldFail() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(VALID_ITEM_ID_1);
        dto.setStart(VALID_AFTER_1_DAY_LDT);
        dto.setEnd(null);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "CreateBookingDto mustn't have end null");
    }

    @Test
    public void validateCreateBookingDto_startInPast_shouldFail() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(VALID_ITEM_ID_1);
        dto.setStart(VALID_BEFORE_1_DAY_LDT);
        dto.setEnd(VALID_BEFORE_1_HOUR_LDT);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "CreateBookingDto mustn't start in past");
    }

    @Test
    public void validateCreateBookingDto_startEqEnd_shouldFail() {
        CreateBookingDto dto = new CreateBookingDto();
        dto.setItemId(VALID_ITEM_ID_1);
        dto.setStart(VALID_AFTER_1_HOUR_LDT);
        dto.setEnd(VALID_AFTER_1_HOUR_LDT);
        Set<ConstraintViolation<CreateBookingDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "CreateBookingDto mustn't have equal start end");
    }

}
