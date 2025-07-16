package com.skillhub.backend.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/api/test")
    public String healthCheck() {
        return "OK";
    }
}
