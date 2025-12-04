package com.irico.backend.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import com.irico.backend.job.exception.JobNotFoundException;
import com.irico.backend.organizations.exception.OrganizationNotFoundException;
import com.irico.backend.source.exception.SourceAlreadyExistsException;
import com.irico.backend.source.exception.SourceNotFoundException;
import com.irico.backend.task.exception.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import io.jsonwebtoken.MalformedJwtException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 Custom exceptions
    @ExceptionHandler(SourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSourceNotFound(SourceNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "SOURCE_NOT_FOUND", ex);
    }

    @ExceptionHandler(SourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSourceAlreadyExists(SourceAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, "SOURCE_ALREADY_EXISTS", ex);
    }

    @ExceptionHandler(SourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleLocationWrongType(SourceAlreadyExistsException ex) {
        return buildError(HttpStatus.CONFLICT, "SOURCE_ALREADY_EXISTS", ex);
    }

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJobNotFound(JobNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "JOB_NOT_FOUND", ex);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskNotFound(TaskNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "TASK_NOT_FOUND", ex);
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrganizationNotFound(OrganizationNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND", ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_INPUT", ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", ex);
    }

    // Malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "INVALID_JSON", ex);
    }

    // Security errors
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return buildError(HttpStatus.FORBIDDEN, "FORBIDDEN", ex);
    }

    // Fallback for unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                ex);
    }

    // Token expired
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED",
                ex);
    }

    // Invalid signature
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "INVALID_SIGNATURE",
                ex);
    }

    // Fallback for other JWT errors
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        return buildError(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN",
                ex);
    }

    // Utility method
    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String code, Exception ex) {
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException manve = (MethodArgumentNotValidException) ex;
            for (FieldError error : manve.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        } else {
            errors.put("message", ex.getMessage());
        }
        ErrorResponse error = new ErrorResponse(
                status.value(),
                code,
                errors,
                LocalDateTime.now());
        return new ResponseEntity<>(error, status);
    }
}
