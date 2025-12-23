package com.example.Custom_Rate_Limiter.Controller;

import com.example.Custom_Rate_Limiter.CustomRateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @CustomRateLimiter(limit = 2, windowSeconds = 10)
    @GetMapping("/test")
    public String test()
    {
        return "Request allowed";
    }
}
