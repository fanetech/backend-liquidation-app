package com.example.demoQrcode.service;

import com.example.demoQrcode.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerService {
	Page<Customer> list(Pageable pageable);
	Optional<Customer> get(Long id);
	Customer create(Customer customer);
	Optional<Customer> update(Long id, Customer customer);
	boolean delete(Long id);
	Page<Customer> search(String term, Pageable pageable);
}


