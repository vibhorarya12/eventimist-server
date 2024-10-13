package com.eventimist.server.exceptions;

public class EntityNotFoundException extends  RuntimeException{
    private static final long serialVerisionUID = 1;
    public EntityNotFoundException (String message){
        super(message);
    }
}
