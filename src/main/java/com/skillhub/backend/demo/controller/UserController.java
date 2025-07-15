package com.skillhub.backend.demo.controller;

import com.skillhub.backend.demo.auth.JwtService;
import com.skillhub.backend.demo.dto.OwnerProfileDTO;
import com.skillhub.backend.demo.dto.UserProfileDTO;
import com.skillhub.backend.demo.model.User;
import com.skillhub.backend.demo.repository.ResourceRepository;
import com.skillhub.backend.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final ResourceRepository resourceRepo;

    @Value("${upload.path}")
    private String uploadPath;

    // 🔍 View other user profile
    @GetMapping("/{id}/profile")
    public OwnerProfileDTO getUserProfile(@PathVariable Long id) {
        var user = userRepo.findById(id).orElseThrow();
        var resources = resourceRepo.findAll()
                .stream()
                .filter(r -> r.getOwner().getId().equals(id))
                .toList();

        return new OwnerProfileDTO(
                user.getId(),
                user.getName(),
                user.getBio(),
                user.getGender(),
                user.getWhatsapp(),
                user.getInstagram(),
                user.getProfilePic(),
                resources
        );
    }

    // 👤 Get logged-in user's profile
    @GetMapping("/me")
    public UserProfileDTO getMyProfile(@AuthenticationPrincipal User user) {
        var resources = resourceRepo.findAll()
                .stream()
                .filter(r -> r.getOwner().getId().equals(user.getId()))
                .toList();

        UserProfileDTO dto = new UserProfileDTO();
        dto.setName(user.getName());
        dto.setWhatsapp(user.getWhatsapp());
        dto.setInstagram(user.getInstagram());
        dto.setProfilePic(user.getProfilePic());
        dto.setBio(user.getBio());
        dto.setGender(user.getGender());
        dto.setResources(resources);
        return dto;
    }

    // 📝 Update logged-in user's profile
    @PutMapping(value = "/me", consumes = {"multipart/form-data"})
    public String updateMyProfile(@AuthenticationPrincipal User user,
                                  @RequestPart("data") UserProfileDTO dto,
                                  @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        user.setName(dto.getName());
        user.setWhatsapp(dto.getWhatsapp());
        user.setInstagram(dto.getInstagram());
        user.setBio(dto.getBio());
        user.setGender(dto.getGender());

        if (image != null && !image.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String savePath = uploadPath + File.separator + fileName;
            Files.copy(image.getInputStream(), Paths.get(savePath));
            user.setProfilePic("/uploads/" + fileName);
        }

        userRepo.save(user);
        return "Profile updated successfully";
    }

    // ❌ Delete logged-in user's profile picture
    @DeleteMapping("/me/profile-pic")
    public String deleteProfilePic(@AuthenticationPrincipal User user) {
        if (user.getProfilePic() != null) {
            File file = new File(uploadPath + user.getProfilePic());
            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println(deleted
                        ? "🧽 Profile picture deleted from disk"
                        : "⚠️ Failed to delete profile picture from disk");
            }

            user.setProfilePic(null);
            userRepo.save(user);
        }

        return "Profile picture deleted successfully";
    }
}
