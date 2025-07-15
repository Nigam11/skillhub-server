package com.skillhub.backend.demo.auth;

import com.skillhub.backend.demo.model.PasswordResetToken;
import com.skillhub.backend.demo.model.User;
import com.skillhub.backend.demo.repository.PasswordResetTokenRepository;
import com.skillhub.backend.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepo;
    private final EmailService emailService;

    // 1️⃣ Send Reset Link to email
    @Transactional
    public void sendResetLink(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // ✅ Delete old token if any exists for this user
        tokenRepo.deleteByUser(user);

        // ✅ Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        // ✅ Save token to DB
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(token)
                .expiry(expiry)
                .build();

        tokenRepo.save(resetToken);

        // ✅ Send email
        emailService.sendResetEmailAsync(email, token);
    }

    // 2️⃣ Reset password using token
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (resetToken.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // ✅ Remove the token after successful reset
        tokenRepo.delete(resetToken);
    }
}
