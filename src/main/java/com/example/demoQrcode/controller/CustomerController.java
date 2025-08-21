package com.example.demoQrcode.controller;

import com.example.demoQrcode.entity.Customer;
import com.example.demoQrcode.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	// GET /api/customers (paginated list)
	@GetMapping
	public Page<Customer> list(@RequestParam(defaultValue = "0") int page,
	                          @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return customerService.list(pageable);
	}

	// GET /api/customers/{id}
	@GetMapping("/{id}")
	public ResponseEntity<?> getOne(@PathVariable Long id) {
		Optional<Customer> c = customerService.get(id);
		return c.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// POST /api/customers
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody Customer customer) {
		Customer created = customerService.create(customer);
		return ResponseEntity.created(URI.create("/api/customers/" + created.getId())).body(created);
	}

	// PUT /api/customers/{id}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
		return customerService.update(id, customer)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	// DELETE /api/customers/{id}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		boolean deleted = customerService.delete(id);
		return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}

	// GET /api/customers/search?q={term}
	@GetMapping("/search")
	public Page<Customer> search(@RequestParam(name = "q", required = false, defaultValue = "") String term,
	                            @RequestParam(defaultValue = "0") int page,
	                            @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return customerService.search(term, pageable);
	}
}


