package com.example.demoQrcode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.cors")
public class CorsConfig {

    private List<String> allowedOrigins = Arrays.asList(
        "http://localhost:5173",    // Vite dev server
        "http://localhost:3000",    // React dev server
        "http://localhost:4200",    // Angular dev server
        "http://localhost:8080"     // Backend
    );

    private List<String> allowedMethods = Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    );

    private List<String> allowedHeaders = Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With", 
        "Accept", "Origin", "Access-Control-Request-Method",
        "Access-Control-Request-Headers", "X-Test-Type", "X-Test-Index"
    );

    private List<String> exposedHeaders = Arrays.asList(
        "Authorization", "Content-Type", "X-Test-Type", "X-Test-Index"
    );

    private boolean allowCredentials = true;
    private long maxAge = 3600L;

    // Bean supprimé pour éviter le conflit avec SecurityConfig
    // La configuration CORS est maintenant gérée dans SecurityConfig

    // Getters et Setters pour la configuration
    public List<String> getAllowedOrigins() { return allowedOrigins; }
    public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }

    public List<String> getAllowedMethods() { return allowedMethods; }
    public void setAllowedMethods(List<String> allowedMethods) { this.allowedMethods = allowedMethods; }

    public List<String> getAllowedHeaders() { return allowedHeaders; }
    public void setAllowedHeaders(List<String> allowedHeaders) { this.allowedHeaders = allowedHeaders; }

    public List<String> getExposedHeaders() { return exposedHeaders; }
    public void setExposedHeaders(List<String> exposedHeaders) { this.exposedHeaders = exposedHeaders; }

    public boolean isAllowCredentials() { return allowCredentials; }
    public void setAllowCredentials(boolean allowCredentials) { this.allowCredentials = allowCredentials; }

    public long getMaxAge() { return maxAge; }
    public void setMaxAge(long maxAge) { this.maxAge = maxAge; }
}
