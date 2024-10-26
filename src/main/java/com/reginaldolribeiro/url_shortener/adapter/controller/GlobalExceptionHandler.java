package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.reginaldolribeiro.url_shortener.adapter.controller.exception.*;
import com.reginaldolribeiro.url_shortener.app.exception.IdGenerationException;
import com.reginaldolribeiro.url_shortener.app.exception.ShortUrlMalformedException;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            InvalidUrlException.class,
            UrlNullableException.class,
            ShortUrlMalformedException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UrlDisabledException.class)
    public ResponseEntity<ErrorResponse> handleUrlDisabledException(UrlDisabledException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.GONE.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);
    }

    @ExceptionHandler(IdGenerationException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(IdGenerationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.PRECONDITION_FAILED.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Constraint violations", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}