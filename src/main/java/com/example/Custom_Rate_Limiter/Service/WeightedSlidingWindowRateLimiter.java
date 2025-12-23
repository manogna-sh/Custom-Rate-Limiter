package com.example.Custom_Rate_Limiter.Service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class WeightedSlidingWindowRateLimiter {

    private final RedisTemplate<String, String> redisTemplate;

    public WeightedSlidingWindowRateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allowRequest(String key, int limit, int windowSeconds) {

        long now = System.currentTimeMillis();
        long windowSizeMs = windowSeconds * 1000L;

        long currentWindowStart = now - (now % windowSizeMs);
        long previousWindowStart = currentWindowStart - windowSizeMs;

        String currentKey = key + ":current";
        String currentTsKey = currentKey + ":ts";
        String previousKey = key + ":previous";

        String storedTs = redisTemplate.opsForValue().get(currentTsKey);
        Long storedWindowStart = storedTs == null ? null : Long.parseLong(storedTs);

        // Rotate window
        if (storedWindowStart == null || storedWindowStart < currentWindowStart) {
            rotateWindow(currentKey, currentTsKey, previousKey, currentWindowStart);
        }

        // Increment current window
        long currentCount = redisTemplate.opsForValue().increment(currentKey);

        // Fetch previous count
        String prev = redisTemplate.opsForValue().get(previousKey);
        long previousCount = prev == null ? 0 : Long.parseLong(prev);

        double elapsed = (double) (now - currentWindowStart);
        double weight = (windowSizeMs - elapsed) / windowSizeMs;

        double effectiveCount = previousCount * weight + currentCount;

        return effectiveCount <= limit;
    }

    private void rotateWindow(
            String currentKey,
            String currentTsKey,
            String previousKey,
            long currentWindowStart) {

        String currentVal = redisTemplate.opsForValue().get(currentKey);

        if (currentVal != null) {
            redisTemplate.opsForValue().set(previousKey, currentVal);
            redisTemplate.expire(previousKey, 2, TimeUnit.MINUTES);
        }

        redisTemplate.opsForValue().set(currentKey, "0");
        redisTemplate.opsForValue().set(currentTsKey, String.valueOf(currentWindowStart));

        redisTemplate.expire(currentKey, 2, TimeUnit.MINUTES);
        redisTemplate.expire(currentTsKey, 2, TimeUnit.MINUTES);
    }
}
