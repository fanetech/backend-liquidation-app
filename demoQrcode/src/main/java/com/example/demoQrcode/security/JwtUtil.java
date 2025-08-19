package com.example.demoQrcode.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;
    
    private SecretKey secretKey;

    @PostConstruct
    public void checkSecret() {
        if (secret == null || secret.isEmpty()) {
            System.err.println("❌ ERREUR : La clé secrète JWT est vide ou nulle !");
        } else {
            String preview = secret.length() > 10 ? secret.substring(0,10) + "..." : secret;
            System.out.println("🔑 Clé JWT chargée (prefix): " + preview);
        }

        if (secret != null && secret.length() < 32) {
            System.out.println("⚠️ WARNING: jwt.secret is short (<32). Use a longer secret for better security.");
        }
        
        // Initialiser la clé secrète
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            // token expiré -> on retourne null pour signaler l'état
            return null;
        } catch (JwtException | IllegalArgumentException e) {
            // token invalide ou malformé
            return null;
        }
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        if (expiration == null) return true;
        return expiration.before(new Date());
    }

    public boolean validateToken(String token, String username) {
        if (token == null || username == null) return false;
        try {
            String tokenUsername = getUsernameFromToken(token);
            if (tokenUsername == null) return false;
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
