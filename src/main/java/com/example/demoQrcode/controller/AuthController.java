package com.example.demoQrcode.controller;

import com.example.demoQrcode.dto.AuthRequest;
import com.example.demoQrcode.dto.AuthResponse;
import com.example.demoQrcode.dto.RegisterRequest;
import com.example.demoQrcode.entity.Role;
import com.example.demoQrcode.entity.User;
import com.example.demoQrcode.repository.RoleRepository;
import com.example.demoQrcode.repository.UserRepository;
import com.example.demoQrcode.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * LOGIN : Authentifie l'utilisateur et retourne un token JWT + role + redirect
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {
		try {
			// Validation des données d'entrée
			if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
				return ResponseEntity.badRequest().body("Le nom d'utilisateur est obligatoire");
			}
			if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
				return ResponseEntity.badRequest().body("Le mot de passe est obligatoire");
			}

			// Authentifier
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
			);

			// Générer le token
			String token = jwtUtil.generateToken(request.getUsername());

			// Déterminer le rôle principal et la redirection suggérée
			Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
			String role = userOpt.filter(u -> u.getRoles() != null && !u.getRoles().isEmpty())
					.map(u -> u.getRoles().stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName())) ? "ADMIN" : "USER")
					.orElse("USER");
			String redirect = "ADMIN".equals(role) ? "/admin" : "/user";
			log.info("token => {}", token);

			return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.body(new AuthResponse(token, request.getUsername(), role, redirect));

		} catch (BadCredentialsException ex) {
			return ResponseEntity.status(401).body("Nom d'utilisateur ou mot de passe invalide");
		} catch (Exception ex) {
			return ResponseEntity.status(500).body("Erreur interne du serveur: " + ex.getMessage());
		}
	}

	// REGISTER USER
	@PostMapping("/register/user")
	public ResponseEntity<?> registerUser(@RequestBody @jakarta.validation.Valid RegisterRequest request) {
		if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Le nom d'utilisateur est obligatoire");
		}
		if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Le mot de passe est obligatoire");
		}
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body("Nom d'utilisateur déjà pris");
		}
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEnabled(true);
		Set<Role> roles = new HashSet<>();
		Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
		roles.add(userRole);
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok("Utilisateur enregistré avec succès en tant que USER");
	}

	// REGISTER ADMIN
	@PostMapping("/register/admin")
	public ResponseEntity<?> registerAdmin(@RequestBody @jakarta.validation.Valid RegisterRequest request) {
		if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Le nom d'utilisateur est obligatoire");
		}
		if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Le mot de passe est obligatoire");
		}
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body("Nom d'utilisateur déjà pris");
		}
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEnabled(true);
		Set<Role> roles = new HashSet<>();
		Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
		roles.add(adminRole);
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok("Utilisateur enregistré avec succès en tant que ADMIN");
	}
}
