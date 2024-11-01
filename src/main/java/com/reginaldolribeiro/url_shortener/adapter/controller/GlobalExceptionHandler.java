package com.reginaldolribeiro.url_shortener.adapter.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reginaldolribeiro.url_shortener.adapter.controller.url.*;
import com.reginaldolribeiro.url_shortener.adapter.helper.ObservabilityHelper;
import com.reginaldolribeiro.url_shortener.adapter.repository.url.UrlSaveDatabaseException;
import com.reginaldolribeiro.url_shortener.adapter.repository.url.UrlSearchDatabaseException;
import com.reginaldolribeiro.url_shortener.adapter.repository.user.UserSaveDatabaseException;
import com.reginaldolribeiro.url_shortener.adapter.repository.user.UserSearchDatabaseException;
import com.reginaldolribeiro.url_shortener.app.exception.IdGenerationException;
import com.reginaldolribeiro.url_shortener.app.exception.ShortUrlMalformedException;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final ObservabilityHelper observabilityHelper;

    public GlobalExceptionHandler(ObservabilityHelper observabilityHelper) {
        this.observabilityHelper = observabilityHelper;
    }


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) throws JsonProcessingException {
        log.error("An user not found error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        var responseEntity = new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        observabilityHelper.addResponseBody(responseEntity);
        return responseEntity;
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException ex) {
        log.error("An URL not found error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            InvalidUrlException.class,
            UrlNullableException.class,
            ShortUrlMalformedException.class,
            InvalidUuidException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception ex) {
        log.error("A bad request error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UrlDisabledException.class)
    public ResponseEntity<ErrorResponse> handleUrlDisabledException(UrlDisabledException ex) {
        log.error("An URL disabled error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.GONE.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.GONE);
    }

    @ExceptionHandler(IdGenerationException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(IdGenerationException ex) {
        log.error("An ID generation error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.PRECONDITION_FAILED.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("A method argument not valid error occurred: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(ConstraintViolationException ex) {
        log.error("A constraint violation error occurred: {}", ex.getMessage(), ex);
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Constraint violations", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            UserSearchDatabaseException.class,
            UserSaveDatabaseException.class,
            UrlSearchDatabaseException.class,
            UrlSaveDatabaseException.class
    })
    public ResponseEntity<ErrorResponse> handleDatabaseExceptions(Exception ex) {
        log.error("A database error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}