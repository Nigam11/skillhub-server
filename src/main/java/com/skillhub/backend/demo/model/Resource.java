package com.skillhub.backend.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String platform;
    private int price;

    private String contactWhatsapp;
    private String contactInstagram;

    private String courseLink;
    private String courseImagePath;

    private double averageRating; // ✅ Store average rating

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    // ✅ Who saved this resource (Many-to-Many inverse side)
    @ManyToMany(mappedBy = "savedResources")
    @JsonIgnore
    private Set<User> savedByUsers = new HashSet<>();

    // ✅ List of all ratings for this resource
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceRating> ratings = new ArrayList<>();
}
