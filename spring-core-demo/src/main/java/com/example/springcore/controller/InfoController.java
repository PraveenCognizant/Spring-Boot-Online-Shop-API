package com.example.springcore.controller;

import com.example.springcore.config.AppProperties;
import com.example.springcore.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

/**
 * ============================================================
 * CONCEPT: @Value + Environment + AppProperties — Reading Config
 * ============================================================
 *
 * THREE ways to read application.properties:
 *
 * 1. @Value("${property.key}")
 *    → Injects a single value directly into a field or parameter.
 *    → @Value("${app.name}") String name
 *
 * 2. AppProperties (our @ConfigurationProperties class)
 *    → Type-safe, grouped, IDE-friendly.
 *
 * 3. Environment (Spring's env abstraction)
 *    → Programmatic access to any property + active profiles.
 *    → Useful when property name is dynamic.
 *
 * Try: GET http://localhost:8080/api/info
 */
@RestController
@RequestMapping("/api/info")
public class InfoController {

    // Method 1: @Value injection
    @Value("${app.name:Spring Boot Demo}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    // Method 2: Type-safe config properties
    private final AppProperties appProperties;

    // Method 3: Spring Environment
    private final Environment environment;

    @Autowired
    public InfoController(AppProperties appProperties, Environment environment) {
        this.appProperties = appProperties;
        this.environment = environment;
    }

    // GET /api/info — Shows app info and active profiles
    @GetMapping
    public ApiResponse<Map<String, Object>> getInfo() {
        return ApiResponse.success(Map.of(
                "appName",        appName,
                "appVersion",     appVersion,
                "currency",       appProperties.getCurrency() != null ? appProperties.getCurrency() : "USD",
                "supportEmail",   appProperties.getSupportEmail() != null ? appProperties.getSupportEmail() : "N/A",
                "activeProfiles", Arrays.asList(environment.getActiveProfiles()),
                "javaVersion",    System.getProperty("java.version"),
                "description",    "Spring Boot Online Shop Demo — covers IoC, REST, JPA, AOP, Events, Scheduling"
        ));
    }
}
