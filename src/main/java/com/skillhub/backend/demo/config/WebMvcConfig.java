
// ✅ WebMvcConfig.java
        package com.skillhub.backend.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serves files under /uploads/** from filesystem location
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/Users/nigamchaudhary/uploads/");
    }
}