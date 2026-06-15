package com.eventimist.server.service;

public interface RateLimiterService {

    boolean tryConsume(
            String key,
            long capacity,
            long refillTokens
    );
}
