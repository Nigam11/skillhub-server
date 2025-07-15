package com.skillhub.backend.demo.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String whatsapp;
    private String instagram;
    private String profilePic;
}
