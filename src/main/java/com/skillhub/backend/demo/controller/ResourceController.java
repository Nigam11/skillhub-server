package com.skillhub.backend.demo.controller;

import com.skillhub.backend.demo.auth.JwtService;
import com.skillhub.backend.demo.dto.PublicResourceDTO;
import com.skillhub.backend.demo.dto.ResourceDetailDTO;
import com.skillhub.backend.demo.model.Resource;
import com.skillhub.backend.demo.model.User;
import com.skillhub.backend.demo.repository.ResourceRepository;
import com.skillhub.backend.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@CrossOrigin
public class ResourceController {

    private final ResourceRepository resourceRepo;
    private final UserRepository userRepo;
    private final JwtService jwtService;

    @Value("${upload.path}")
    private String uploadPath;

    // 🟢 Share a new resource
    @PostMapping(consumes = {"multipart/form-data"})
    public Resource shareResource(
            @RequestPart("resource") Resource resource,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestHeader("Authorization") String authHeader) throws IOException {

        String jwt = authHeader.substring(7);
        String email = jwtService.extractUsername(jwt);
        User owner = userRepo.findByEmail(email).orElseThrow();

        resource.setOwner(owner);
        resource.setContactWhatsapp(owner.getWhatsapp());
        resource.setContactInstagram(owner.getInstagram());

        if (image != null && !image.isEmpty()) {
            String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
            String uploadDir = uploadPath + "/resources/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            File savedFile = new File(uploadDir + filename);
            image.transferTo(savedFile);
            resource.setCourseImagePath("/uploads/resources/" + filename);
        }

        return resourceRepo.save(resource);
    }

    // 🔁 Update a resource
    @PutMapping("/{id}")
    public Resource updateResource(@PathVariable Long id, @RequestBody Resource updated, @RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        String email = jwtService.extractUsername(jwt);
        User currentUser = userRepo.findByEmail(email).orElseThrow();

        Resource existing = resourceRepo.findById(id).orElseThrow();
        if (!existing.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setPlatform(updated.getPlatform());
        existing.setPrice(updated.getPrice());
        existing.setCourseLink(updated.getCourseLink());

        return resourceRepo.save(existing);
    }

    // ❌ Delete a resource
    @DeleteMapping("/{id}")
    public String deleteResource(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        String email = jwtService.extractUsername(jwt);
        User currentUser = userRepo.findByEmail(email).orElseThrow();

        Resource resource = resourceRepo.findById(id).orElseThrow();
        if (!resource.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        resourceRepo.delete(resource);
        return "Resource deleted successfully.";
    }

    // 🔍 Get all public resources
    @GetMapping
    public List<PublicResourceDTO> getAllResources(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String requesterId = extractRequesterId(authHeader);
        return mapToDTO(resourceRepo.findAll(), requesterId);
    }

    // 🔍 Search by title and/or platform
    @GetMapping("/search")
    public List<PublicResourceDTO> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String platform,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        System.out.println("🔐 Authorization Received in /search: " + authHeader);

        String requesterId = extractRequesterId(authHeader);
        List<Resource> resources = resourceRepo.findAll();

        if (title != null && !title.isBlank()) {
            resources = resources.stream()
                    .filter(r -> r.getTitle().toLowerCase().contains(title.toLowerCase()))
                    .toList();
        }

        if (platform != null && !platform.isBlank()) {
            resources = resources.stream()
                    .filter(r -> r.getPlatform() != null && r.getPlatform().equalsIgnoreCase(platform))
                    .toList();
        }

        return mapToDTO(resources, requesterId);
    }

    // 🔍 Filter by platform
    @GetMapping("/filter/platform")
    public List<PublicResourceDTO> filterByPlatform(
            @RequestParam String platform,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String requesterId = extractRequesterId(authHeader);
        return mapToDTO(resourceRepo.findByPlatformContainingIgnoreCase(platform), requesterId);
    }

    // 🔍 Filter by price
    @GetMapping("/filter/price")
    public List<PublicResourceDTO> filterByPrice(
            @RequestParam int maxPrice,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String requesterId = extractRequesterId(authHeader);
        return mapToDTO(resourceRepo.findByPriceLessThanEqual(maxPrice), requesterId);
    }

    // 🔍 Get detailed view
    @GetMapping("/{id}")
    public ResourceDetailDTO getResource(@PathVariable Long id) {
        Resource resource = resourceRepo.findById(id).orElseThrow();
        User owner = resource.getOwner();

        return new ResourceDetailDTO(
                resource.getId(),
                resource.getTitle(),
                resource.getDescription(),
                resource.getPlatform(),
                resource.getPrice(),
                resource.getCourseLink(),
                resource.getCourseImagePath(),
                owner.getName(),
                owner.getProfilePic(),
                owner.getWhatsapp(),
                owner.getInstagram()
        );
    }

    // 🔧 Extract user ID from JWT header
    private String extractRequesterId(String authHeader) {
        System.out.println("🔍 Incoming Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String jwt = authHeader.substring(7);
                String email = jwtService.extractUsername(jwt);
                System.out.println("📧 Extracted email: " + email);

                User user = userRepo.findByEmail(email).orElse(null);
                if (user != null) {
                    System.out.println("✅ Found user ID: " + user.getId());
                    return user.getId().toString();
                } else {
                    System.out.println("❌ No user found for email");
                }
            } catch (Exception e) {
                System.out.println("❌ Error in extractRequesterId:");
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ No Authorization header or wrong format");
        }

        return null;
    }

    // 🔁 Convert Resource to DTO + calculate isOwner
    private List<PublicResourceDTO> mapToDTO(List<Resource> resources, String requesterId) {
        return resources.stream().map(resource -> {
            User owner = resource.getOwner();

            boolean isOwner = requesterId != null &&
                    owner.getId().toString().equals(requesterId);

            System.out.println("🔎 Mapping Resource ID: " + resource.getId() +
                    " | Owner ID: " + owner.getId() +
                    " | Requester ID: " + requesterId +
                    " | isOwner: " + isOwner);

            return new PublicResourceDTO(
                    resource.getId(),
                    resource.getTitle(),
                    resource.getDescription(),
                    resource.getPlatform(),
                    resource.getPrice(),
                    resource.getCourseLink(),
                    resource.getCourseImagePath(),
                    owner.getId(),
                    owner.getName(),
                    owner.getProfilePic(),
                    owner.getWhatsapp(),
                    owner.getInstagram(),
                    isOwner
            );
        }).toList();
    }
}
