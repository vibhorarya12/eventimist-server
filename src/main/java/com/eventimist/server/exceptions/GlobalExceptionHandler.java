package com.eventimist.server.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;


@ControllerAdvice
public class GlobalExceptionHandler {
@ExceptionHandler(EntityNotFoundException.class)
public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex , WebRequest webRequest){

        ErrorResponse errorResponse  = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setTimestamp(new Date());
        return  new ResponseEntity<ErrorResponse>(errorResponse , HttpStatus.NOT_FOUND);
}

@ExceptionHandler(WrongCredentialsException.class)
public ResponseEntity<ErrorResponse> handleWrongCredentialsException(WrongCredentialsException ex , WebRequest webRequest){
       ErrorResponse errorResponse  = new ErrorResponse();
       errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
       errorResponse.setMessage(ex.getMessage());
       errorResponse.setTimestamp(new Date());
       return  new ResponseEntity<ErrorResponse>(errorResponse , HttpStatus.UNAUTHORIZED);

}
@ExceptionHandler(ExistingEntityException.class)
public ResponseEntity<ErrorResponse> handleExistingEntityException(ExistingEntityException ex , WebRequest webRequest){
       ErrorResponse errorResponse  = new ErrorResponse();
       errorResponse.setStatusCode(HttpStatus.CONFLICT.value());
       errorResponse.setMessage(ex.getMessage());
       errorResponse.setTimestamp(new Date());
       return  new ResponseEntity<ErrorResponse>(errorResponse , HttpStatus.CONFLICT);

}
@ExceptionHandler(ImageUploadException.class)
 public  ResponseEntity<ErrorResponse> handleImageUploadException(ImageUploadException ex , WebRequest webRequest){
    ErrorResponse errorResponse =  new ErrorResponse();
    errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
    errorResponse.setMessage(ex.getMessage());
    errorResponse.setTimestamp(new Date());
    return  new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
}

@ExceptionHandler (ClerkAuthSessionException.class)
public  ResponseEntity<ErrorResponse> handleClerkAuthSessionException (ClerkAuthSessionException ex , WebRequest webRequest){
    ErrorResponse errorResponse =  new ErrorResponse();
    errorResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
    errorResponse.setMessage(ex.getMessage());
    errorResponse.setTimestamp(new Date());
    return  new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
}
@ExceptionHandler (JwtAuthException.class)
public  ResponseEntity<ErrorResponse> handleJwtAuthException (JwtAuthException ex , WebRequest webRequest){
    ErrorResponse errorResponse =  new ErrorResponse();
    errorResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
    errorResponse.setMessage(ex.getMessage());
    errorResponse.setTimestamp(new Date());
    return  new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.UNAUTHORIZED);
}

}
