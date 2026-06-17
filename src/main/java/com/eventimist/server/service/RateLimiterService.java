package com.eventimist.server.service;

public interface RateLimiterService {

    void checkRateLimit(
            String key,
            long requestsPerMinute
    );

}