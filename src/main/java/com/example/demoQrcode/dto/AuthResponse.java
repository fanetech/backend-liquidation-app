package com.example.demoQrcode.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String role;      // "ADMIN" ou "USER"
    private String redirect;  // chemin suggéré côté client: "/admin" ou "/user"

    public AuthResponse() {}

    public AuthResponse(String token) { this.token = token; }

    public AuthResponse(String token, String username, String role, String redirect) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.redirect = redirect;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRedirect() { return redirect; }
    public void setRedirect(String redirect) { this.redirect = redirect; }
}
