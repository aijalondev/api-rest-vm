package com.api_rest_vm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.allowed-methods}")
    private String allowedMethods;

    @Value("${cors.allow-credentials}")
    private boolean allowCredentials;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        registry.addMapping("/auth/**")
                .allowedOrigins(origins)
                .allowedMethods(allowedMethods)
                .allowedHeaders("*")
                .allowCredentials(allowCredentials)
                .maxAge(3600);
        registry.addMapping("/user/**")
                .allowedOrigins(origins)
                .allowedMethods(allowedMethods)
                .allowedHeaders("*")
                .allowCredentials(allowCredentials)
                .maxAge(3600);
    }
}