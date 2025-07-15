package com.skillhub.backend.demo.controller;

import com.skillhub.backend.demo.auth.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-email")
@RequiredArgsConstructor
@CrossOrigin
public class EmailTestController {

    private final EmailService emailService;

    @PostMapping
    public String sendTestEmail(@RequestParam String to) {
        emailService.sendResetEmailAsync(to, "dummy-reset-token-123456");
        return "Test email sent to: " + to;
    }
}
