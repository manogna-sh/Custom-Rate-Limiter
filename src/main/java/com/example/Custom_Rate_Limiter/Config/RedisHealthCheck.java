package com.example.Custom_Rate_Limiter.Config;

import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisHealthCheck {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisHealthCheck(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void testRedis() {
        redisTemplate.opsForValue().set("test", "ok");
        System.out.println("Redis value: " + redisTemplate.opsForValue().get("test"));
    }
}
