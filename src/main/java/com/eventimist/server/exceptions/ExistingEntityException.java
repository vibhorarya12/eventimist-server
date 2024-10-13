package com.eventimist.server.exceptions;

public class ExistingEntityException extends  RuntimeException{

    public ExistingEntityException (String message){
         super(message);
    }
}
