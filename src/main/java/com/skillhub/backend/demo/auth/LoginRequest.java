package com.skillhub.backend.demo.auth;

//package com.skillhub.backend.demo.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}

