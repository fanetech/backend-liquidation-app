package com.example.demoQrcode.repository;


import com.example.demoQrcode.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}