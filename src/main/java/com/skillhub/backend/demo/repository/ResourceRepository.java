package com.skillhub.backend.demo.repository;

import com.skillhub.backend.demo.model.Resource;
import com.skillhub.backend.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByTitleContainingIgnoreCase(String title);
    List<Resource> findByPlatformContainingIgnoreCase(String platform);
    List<Resource> findByPriceLessThanEqual(int price);
    List<Resource> findByOwner(User owner);
}
