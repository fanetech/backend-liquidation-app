package com.example.demoQrcode.controller;


import com.example.demoQrcode.entity.Product;
import com.example.demoQrcode.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // Tout utilisateur authentifié (ROLE_USER ou ROLE_ADMIN) peut voir la liste
    @GetMapping
    public List<Product> all() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        Optional<Product> p = productRepository.findById(id);
        return p.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Seul ROLE_ADMIN peut créer/éditer/supprimer
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public Product create(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(product.getName());
            existing.setDescription(product.getDescription());
            existing.setPrice(product.getPrice());
            productRepository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
