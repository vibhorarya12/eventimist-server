package com.eventimist.server.service;

public interface UserActionsService {

    void bookmarkEvents (Long userId ,  Long eventId);
    void attendEvents (Long userId , Long eventId);
}
