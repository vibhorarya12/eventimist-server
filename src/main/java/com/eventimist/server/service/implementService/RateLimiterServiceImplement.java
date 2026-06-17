package com.eventimist.server.service.implementService;

import com.eventimist.server.exceptions.BadRequestException;
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
    public void checkRateLimit(
            String key,
            long requestsPerMinute
    ) {

        Bucket bucket =
                buckets.computeIfAbsent(
                        key,
                        k -> Bucket.builder()
                                .addLimit(
                                        Bandwidth.builder()
                                                .capacity(
                                                        requestsPerMinute
                                                )
                                                .refillGreedy(
                                                        requestsPerMinute,
                                                        Duration.ofMinutes(1)
                                                )
                                                .build()
                                )
                                .build()
                );

        if (!bucket.tryConsume(1)) {

            throw new BadRequestException(
                    "Too many requests. Please try again later."
            );
        }
    }
}