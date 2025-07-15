package com.skillhub.backend.demo.dto;

import lombok.Data;

@Data
public class PasswordResetSubmit {
    private String token;
    private String newPassword;
}
