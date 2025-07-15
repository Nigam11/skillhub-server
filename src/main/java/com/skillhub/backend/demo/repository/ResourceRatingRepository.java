package com.skillhub.backend.demo.repository;

import com.skillhub.backend.demo.model.Resource;
import com.skillhub.backend.demo.model.ResourceRating;
import com.skillhub.backend.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRatingRepository extends JpaRepository<ResourceRating, Long> {

    // 🧠 Check if a user has already rated a resource
    Optional<ResourceRating> findByResourceAndUser(Resource resource, User user);

    // 🔍 Find all ratings for a resource (to calculate average)
    List<ResourceRating> findByResource(Resource resource);
}
