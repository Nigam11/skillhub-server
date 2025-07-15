package com.skillhub.backend.demo.config; // ✅ 1. Add correct package name

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize; // ✅ 2. Import DataSize

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10));     // ✅ 3. Use DataSize
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));  // ✅ 4. Use DataSize
        return factory.createMultipartConfig();
    }
}
