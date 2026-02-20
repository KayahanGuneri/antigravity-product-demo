package com.antigravity.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ApiErrorResponse> handleProductNotFoundException(ProductNotFoundException ex,
                        HttpServletRequest request) {
                return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                List<ApiErrorResponse.FieldViolation> errors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(err -> new ApiErrorResponse.FieldViolation(err.getField(),
                                                err.getDefaultMessage()))
                                .collect(Collectors.toList());

                return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex,
                        HttpServletRequest request) {
                List<ApiErrorResponse.FieldViolation> errors = ex.getConstraintViolations()
                                .stream()
                                .map(cv -> new ApiErrorResponse.FieldViolation(cv.getPropertyPath().toString(),
                                                cv.getMessage()))
                                .collect(Collectors.toList());

                return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, errors);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
                        HttpServletRequest request) {
                return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest request) {
                return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", request, null);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {
                return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request", request, null);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
                return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request, null);
        }

        private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message,
                        HttpServletRequest request, List<ApiErrorResponse.FieldViolation> fieldErrors) {
                ApiErrorResponse response = new ApiErrorResponse(
                                Instant.now(),
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                request.getRequestURI(),
                                MDC.get("traceId"),
                                fieldErrors);
                return new ResponseEntity<>(response, status);
        }
}
