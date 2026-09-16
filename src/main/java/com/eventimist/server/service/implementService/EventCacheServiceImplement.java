package com.eventimist.server.service.implementService;

import com.eventimist.server.service.EventCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EventCacheServiceImplement implements EventCacheService {

    private final StringRedisTemplate redisTemplate;

    @Override
    public String getEvent(String slug) {
        return redisTemplate.opsForValue().get("event:slug:" + slug);
    }

    @Override
    public void cacheEvent(String slug, String eventJson) {
        redisTemplate.opsForValue().set(
                "event:slug:" + slug,
                eventJson,
                10,
                TimeUnit.MINUTES
        );
    }
}