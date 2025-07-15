package com.skillhub.backend.demo.dto;

import com.skillhub.backend.demo.model.Resource;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTO {
    private String name;
    private String whatsapp;
    private String instagram;
    private String profilePic;
    private String bio;      // ✅ NEW
    private String gender;   // ✅ NEW
    private List<Resource> resources; // ✅ NEW: User's shared resources
}
