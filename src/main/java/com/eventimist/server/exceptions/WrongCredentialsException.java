package com.eventimist.server.exceptions;

public class WrongCredentialsException extends  RuntimeException{

    public WrongCredentialsException (String message){
        super(message);
    }
}
