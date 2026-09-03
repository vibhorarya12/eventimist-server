package com.eventimist.server.exceptions;

public class RefreshAccessTokenException extends RuntimeException {
    public RefreshAccessTokenException(String message) {
        super(message);
    }
}
