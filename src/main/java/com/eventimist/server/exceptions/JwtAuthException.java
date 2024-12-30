package com.eventimist.server.exceptions;

public class JwtAuthException extends  RuntimeException{
    public  JwtAuthException (String message){
        super(message);
    }
}
