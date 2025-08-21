package com.example.demoQrcode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    // Optionnel: permet de choisir le profil de connexion souhaité: USER ou ADMIN
    // Par défaut: USER si null ou vide
    @Pattern(regexp = "(?i)USER|ADMIN", message = "Le rôle doit être USER ou ADMIN")
    private String role;

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
