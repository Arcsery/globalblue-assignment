package com.globalblue.vatrefund.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    private static final String FRONTEND_ORIGIN = "http://localhost:4200";

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/api/purchases")
                        .allowedOrigins(FRONTEND_ORIGIN)
                        .allowedMethods("POST", "OPTIONS")
                        .allowedHeaders("Content-Type", "Accept")
                        .maxAge(3600);

                registry.addMapping("/api/purchases/*")
                        .allowedOrigins(FRONTEND_ORIGIN)
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("Content-Type", "Accept")
                        .maxAge(3600);
            }
        };
    }
}