package com.example.demoQrcode.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String message = authException == null ? "Unauthorized" : authException.getMessage();
        // Simple JSON — tu peux enrichir avec un timestamp, path, etc.
        String body = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\",\"path\":\"%s\"}",
                escapeJson(message),
                escapeJson(request.getRequestURI()));

        response.getWriter().write(body);
    }

    // Petit utilitaire pour éviter des guillemets mal placés dans le message
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"");
    }
}
