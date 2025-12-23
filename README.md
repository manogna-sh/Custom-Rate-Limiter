# Distributed Rate Limiting

A Spring Boot–based API Rate Limiter implementing a **Distributed Rate Limiter**
using **Redis**, **AOP**, and the **Sliding Window Counter with Weighted Window algorithm**.

## Tech Stack
- Java 17
- Spring Boot 3.2.x
- Redis
- Spring AOP
- Maven

## Features
- Distributed rate limiting
- Weighted sliding window algorithm
- Redis-backed counters
- Annotation-driven configuration
- Clean separation using AOP

## Example
```java
@RateLimit(limit = 2, windowSeconds = 10)
@GetMapping("/test")
public String test() {
    return "Allowed";
}
