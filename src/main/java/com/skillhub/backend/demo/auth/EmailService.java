package com.skillhub.backend.demo.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    @Async
    public void sendResetEmailAsync(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("SkillHub - Password Reset");
            String resetLink = frontendBaseUrl + "/reset-password?token=" + token;
            helper.setText("Click the link to reset your password: " + resetLink);
            mailSender.send(message);
            System.out.println("✅ Reset email sent to " + to);
        } catch (MessagingException e) {
            System.out.println("❌ Failed to send reset email to " + to);
            e.printStackTrace();
        }
    }
}
