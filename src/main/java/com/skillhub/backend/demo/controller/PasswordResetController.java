package com.skillhub.backend.demo.controller;

import com.skillhub.backend.demo.auth.EmailService;
import com.skillhub.backend.demo.dto.PasswordResetRequest;
import com.skillhub.backend.demo.dto.PasswordResetSubmit;
import com.skillhub.backend.demo.model.User;
import com.skillhub.backend.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/reset-password")
@RequiredArgsConstructor
@CrossOrigin
public class PasswordResetController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/request")
    public ResponseEntity<?> requestReset(@RequestBody PasswordResetRequest req) {
        Optional<User> userOpt = userRepository.findByEmail(req.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        // ✅ Send email asynchronously
        emailService.sendResetEmailAsync(user.getEmail(), token);

        return ResponseEntity.ok("Reset email sent");
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitReset(@RequestBody PasswordResetSubmit submit) {
        Optional<User> userOpt = userRepository.findByResetToken(submit.getToken());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid reset token");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(submit.getNewPassword()));
        user.setResetToken(null); // clear token
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful");
    }
}
