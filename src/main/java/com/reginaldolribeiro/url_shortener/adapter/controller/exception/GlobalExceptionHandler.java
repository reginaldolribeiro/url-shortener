package com.reginaldolribeiro.url_shortener.adapter.controller.exception;

import com.reginaldolribeiro.url_shortener.app.exception.IdGenerationException;
import com.reginaldolribeiro.url_shortener.app.exception.InvalidUrlException;
import com.reginaldolribeiro.url_shortener.app.exception.UrlNullableException;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidUrlException.class, UrlNullableException.class})
    public ResponseEntity<ErrorResponse> handleInvalidUrlException(InvalidUrlException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}