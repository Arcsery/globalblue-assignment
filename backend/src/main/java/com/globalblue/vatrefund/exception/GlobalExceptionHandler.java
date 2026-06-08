package com.globalblue.vatrefund.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> messages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        log.warn("Validation failed: {}", messages);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, messages);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("Invalid request: {}", exception.getMessage());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, List.of(exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("Invalid request body: {}", exception.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                List.of("Invalid request body. Please check the JSON format and enum values.")
        );
    }

    @ExceptionHandler(PurchasesNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePurchasesNotFoundException(PurchasesNotFoundException exception) {
        log.warn("Purchases not found: {}", exception.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND, List.of(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception) {
        log.error("Unexpected error occurred", exception);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Unexpected server error occurred.")
        );
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, List<String> messages) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                messages
        );

        return ResponseEntity.status(status).body(response);
    }
}
