package ru.practicum.shareit.common.web.controller;

import ru.practicum.shareit.common.exception.ActionNotPermittedForUserException;
import ru.practicum.shareit.common.exception.DuplicateDataException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.web.dto.ErrorResponseDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception exception) {
        log.error("Unhandled exception {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("Validation exception {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(exception.getBindingResult().getAllErrors().getFirst().getDefaultMessage()));
    }

    @ExceptionHandler(ActionNotPermittedForUserException.class)
    public ResponseEntity<ErrorResponseDto> handleActionNotPermittedForUserException(final Exception exception) {
        log.error("User are not permitted for operation {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto(exception.getMessage()));
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateDataException(Exception ex) {
        log.error("Duplicate data exception", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(final NotFoundException ex) {
        log.error("Not found exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDto(ex.getMessage()));
    }

}
