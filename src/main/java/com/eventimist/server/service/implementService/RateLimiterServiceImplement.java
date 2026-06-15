package com.eventimist.server.service.implementService;

import com.eventimist.server.service.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterServiceImplement
        implements RateLimiterService {

    private final Map<String, Bucket> buckets =
            new ConcurrentHashMap<>();

    @Override
    public boolean tryConsume(
            String key,
            long capacity,
            long refillTokens
    ) {

        Bucket bucket =
                buckets.computeIfAbsent(
                        key,
                        k -> Bucket.builder()
                                .addLimit(
                                        Bandwidth.builder()
                                                .capacity(capacity)
                                                .refillGreedy(
                                                        refillTokens,
                                                        Duration.ofMinutes(1)
                                                )
                                                .build()
                                )
                                .build()
                );

        return bucket.tryConsume(1);
    }
}