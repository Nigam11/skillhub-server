package com.skillhub.backend.demo.dto;

import com.skillhub.backend.demo.model.Resource;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileDTO {
    private Long id; // ✅ Add this line
    private String name;
    private String whatsapp;
    private String instagram;
    private String profilePic;
    private String bio;
    private String gender;
    private List<Resource> resources;
}
