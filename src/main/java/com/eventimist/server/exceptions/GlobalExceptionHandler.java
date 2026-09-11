package com.eventimist.server.exceptions;


import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest webRequest) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WrongCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleWrongCredentialsException(WrongCredentialsException ex, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(ExistingEntityException.class)
    public ResponseEntity<ErrorResponse> handleExistingEntityException(ExistingEntityException ex, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.CONFLICT.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ErrorResponse> handleImageUploadException(ImageUploadException ex, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClerkAuthSessionException.class)
    public ResponseEntity<ErrorResponse> handleClerkAuthSessionException(ClerkAuthSessionException ex, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JwtAuthException.class)
    public ResponseEntity<ErrorResponse> handleJwtAuthException(JwtAuthException ex, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex, WebRequest webRequest) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(new Date());


        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorResponse.setMessage(error.getDefaultMessage())
        );

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(ConstraintViolationException ex) {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setTimestamp(new Date());
        errorResponse.setMessage(ex.getConstraintViolations().stream().map(violation -> violation.getMessage())
                .collect(Collectors.joining(", ")));

        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RefreshAccessTokenException.class)
    public ResponseEntity<ErrorResponse>handleRefreshAccessToken(RefreshAccessTokenException ex , WebRequest webRequest){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.UNAUTHORIZED);

    }


}
