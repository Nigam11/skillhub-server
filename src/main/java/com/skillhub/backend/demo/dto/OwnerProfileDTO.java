package com.skillhub.backend.demo.dto;

import com.skillhub.backend.demo.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OwnerProfileDTO {
    private Long id;
    private String name;
    private String bio;
    private String gender;
    private String whatsapp;
    private String instagram;
    private String profilePic;
    private List<Resource> resources;
}
