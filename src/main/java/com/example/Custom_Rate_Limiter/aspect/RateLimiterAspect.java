package com.example.Custom_Rate_Limiter.aspect;

import com.example.Custom_Rate_Limiter.CustomRateLimiter;
import com.example.Custom_Rate_Limiter.Service.WeightedSlidingWindowRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class RateLimiterAspect {

    private final WeightedSlidingWindowRateLimiter limiter;
    private final HttpServletRequest request;

    public RateLimiterAspect(WeightedSlidingWindowRateLimiter limiter, HttpServletRequest request) {
        this.limiter = limiter;
        this.request = request;
    }

    @Around("@annotation(rateLimit)")
    public Object applyRateLimit(
            ProceedingJoinPoint joinPoint,
            CustomRateLimiter rateLimit) throws Throwable {
        String clientId = resolveClientId();
        String redisKey = "rate:" + clientId;

        boolean allowed = limiter.allowRequest(redisKey, rateLimit.limit(), rateLimit.windowSeconds());

        if(!allowed) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }

        return joinPoint.proceed();
    }

    private String resolveClientId() {
        // Can be IP, API key, JWT subject, etc.
        return request.getRemoteAddr();
    }
}


