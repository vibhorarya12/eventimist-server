package com.eventimist.server.service;

public interface EventCacheService {

    // Get cached event by slug
    String getEvent(String slug);

    // Cache event response by slug
    void cacheEvent(String slug, String eventJson);
}