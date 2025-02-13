package com.eventimist.server.service;

public interface ClerkAuthService {
    boolean AuthenticateClerkSession ( String clerkSessionId , String email);

    boolean deleteExistingClerkUser (String email);
}
