package com.example.demoQrcode.security;

import com.example.demoQrcode.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    // Utiliser l'injection par constructeur (meilleure pratique)
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Ignorer les endpoints publics et les requêtes OPTIONS
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/auth/") || 
            requestURI.startsWith("/error") ||
            "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HEADER);
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith(PREFIX)) {
            token = authHeader.substring(PREFIX.length());
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (ExpiredJwtException e) {
                // token expiré : log et laisser la chaîne continuer (Spring gérera le 401 via EntryPoint si configuré)
                logger.info("JWT expired: " + e.getMessage());
            } catch (Exception e) {
                logger.warn("Unable to parse JWT token: " + e.getMessage());
            }
        }

        // Si on a un username et pas encore d'authentification dans le contexte
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails != null && jwtUtil.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Si loadUserByUsername lève une exception, on log et on continue
                logger.warn("UserDetailsService failed to load user '" + username + "': " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
