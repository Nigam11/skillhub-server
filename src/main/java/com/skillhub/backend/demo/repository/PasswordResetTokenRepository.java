package com.skillhub.backend.demo.repository;

import com.skillhub.backend.demo.model.PasswordResetToken;
import com.skillhub.backend.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUser(User user);

    // ✅ Delete reset token by user
    void deleteByUser(User user);
}
