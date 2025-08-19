package com.example.demoQrcode.service.impl;

import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.repository.CustomerRepository;
import com.example.demoQrcode.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public Page<Customer> list(Pageable pageable) {
		return customerRepository.findAll(pageable);
	}

	@Override
	public Optional<Customer> get(Long id) {
		return customerRepository.findById(id);
	}

	@Override
	public Customer create(Customer customer) {
		if (customerRepository.existsByIfu(customer.getIfu())) {
			throw new IllegalArgumentException("IFU déjà utilisé");
		}
		if (customerRepository.existsByEmail(customer.getEmail())) {
			throw new IllegalArgumentException("E-mail déjà utilisé");
		}
		return customerRepository.save(customer);
	}

	@Override
	public Optional<Customer> update(Long id, Customer customer) {
		return customerRepository.findById(id).map(existing -> {
			// Vérification d'unicité si IFU/Email changent
			if (!existing.getIfu().equals(customer.getIfu()) && customerRepository.existsByIfu(customer.getIfu())) {
				throw new IllegalArgumentException("IFU déjà utilisé");
			}
			if (!existing.getEmail().equals(customer.getEmail()) && customerRepository.existsByEmail(customer.getEmail())) {
				throw new IllegalArgumentException("E-mail déjà utilisé");
			}
			existing.setFirstName(customer.getFirstName());
			existing.setLastName(customer.getLastName());
			existing.setAddress(customer.getAddress());
			existing.setIfu(customer.getIfu());
			existing.setPhone(customer.getPhone());
			existing.setEmail(customer.getEmail());
			return customerRepository.save(existing);
		});
	}

	@Override
	public boolean delete(Long id) {
		if (customerRepository.existsById(id)) {
			customerRepository.deleteById(id);
			return true;
		}
		return false;
	}

	@Override
	public Page<Customer> search(String term, Pageable pageable) {
		return customerRepository.search(term == null ? "" : term, pageable);
	}
}


