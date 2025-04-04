package com.urbanfood.cart.exception;

import com.urbanfood.cart.dto.CartResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CartException.class)
    public ResponseEntity<CartResponseDTO> handleCartException(CartException ex) {
        log.error("Cart exception: {}", ex.getMessage());
        return new ResponseEntity<>(
                CartResponseDTO.error("Something went wrong"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CartResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                CartResponseDTO.error("Validation error: " + errorMessage),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CartResponseDTO> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return new ResponseEntity<>(
                CartResponseDTO.error("Something went wrong"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}