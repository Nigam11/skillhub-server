package com.skillhub.backend.demo.auth;

import com.skillhub.backend.demo.model.Role;
import com.skillhub.backend.demo.model.User;
import com.skillhub.backend.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthResponse signup(SignupRequest request) {
        Role roleToSet = request.getRole() != null ? request.getRole() : Role.USER;

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(roleToSet)
                .whatsapp(request.getWhatsapp())
                .instagram(request.getInstagram())
                .profilePic(request.getProfilePic())
                .build();

        userRepository.save(user);

        var jwt = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .name(user.getName())
                .profilePic(user.getProfilePic())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        var jwt = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .name(user.getName())
                .profilePic(user.getProfilePic())
                .build();
    }

    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No user with email: " + email));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        userRepository.save(user);

        emailService.sendResetEmailAsync(email, token);
        return "Reset password link sent to: " + email;
    }

    public String resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null); // invalidate token
        userRepository.save(user);

        return "Password reset successfully.";
    }
}
