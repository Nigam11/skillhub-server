package com.skillhub.backend.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicResourceDTO {
    private Long id;
    private String title;
    private String description;
    private String platform;
    private int price;
    private String courseLink;
    private String courseImagePath;

    private Long ownerId;
    private String ownerName;
    private String ownerProfilePic;
    private String ownerWhatsapp;
    private String ownerInstagram;

    private boolean isOwner; // ✅ Determines if current user owns this resource
}
