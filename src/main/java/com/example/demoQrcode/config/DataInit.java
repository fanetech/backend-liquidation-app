package com.example.demoQrcode.config;


import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.entity.Role;
import com.example.demoQrcode.entity.User;
import com.example.demoQrcode.repository.CustomerRepository;
import com.example.demoQrcode.repository.RoleRepository;
import com.example.demoQrcode.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInit {

    @Bean
    CommandLineRunner init(RoleRepository roleRepository,
                           UserRepository userRepository,
                           CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder) {
        return args -> {
            // Roles
            Role roleUser = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

            // Users
            if (userRepository.findByUsername("user1").isEmpty()) {
                User u = new User();
                u.setUsername("user1");
                String hashedUserPassword = passwordEncoder.encode("Mot de passe1");
                System.out.println("Mot de passe haché de user1 : " + hashedUserPassword);
                u.setPassword(hashedUserPassword);
                u.setEnabled(true);
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(roleUser);
                u.setRoles(userRoles);
                userRepository.save(u);
            }

            if (userRepository.findByUsername("admin1").isEmpty()) {
                User a = new User();
                a.setUsername("admin1");
                String hashedAdminPassword = passwordEncoder.encode("password1");
                System.out.println("Mot de passe haché de admin1 : " + hashedAdminPassword);
                a.setPassword(hashedAdminPassword);
                a.setEnabled(true);
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(roleAdmin);
                a.setRoles(adminRoles);
                userRepository.save(a);
            }

            // Clients exemples
            if (customerRepository.count() == 0) {
                customerRepository.save(new Customer("DOE", "John", "Cotonou", "IFU123456", "+22997000000", "john.doe@example.com"));
                customerRepository.save(new Customer("DUPONT", "Alice", "Porto-Novo", "IFU654321", "+22966000000", "alice.dupont@example.com"));
            }
        };
    }
}


