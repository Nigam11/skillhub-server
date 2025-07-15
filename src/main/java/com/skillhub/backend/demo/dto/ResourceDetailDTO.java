package com.skillhub.backend.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String platform;
    private int price;
    private String courseLink;         // ✅ added
    private String courseImagePath;    // ✅ added

    private String ownerName;
    private String ownerProfilePic;
    private String ownerWhatsapp;
    private String ownerInstagram;
}
