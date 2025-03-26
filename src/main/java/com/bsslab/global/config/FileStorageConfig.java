package com.bsslab.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.io.File;

@Configuration
public class FileStorageConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public boolean initUploadDirectories() {
        // Create uploads directory if it doesn't exist
        File uploadDir = new File("./uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        // Create uploads/posts directory if it doesn't exist
        File postsDir = new File("./uploads/posts");
        if (!postsDir.exists()) {
            postsDir.mkdir();
        }

        return true;
    }
}