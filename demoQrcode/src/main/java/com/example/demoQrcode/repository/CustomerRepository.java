package com.example.demoQrcode.repository;

import com.example.demoQrcode.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByIfu(String ifu);
	Optional<Customer> findByEmail(String email);

	boolean existsByIfu(String ifu);
	boolean existsByEmail(String email);

	@Query("SELECT c FROM Customer c WHERE " +
			"LOWER(c.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
			"LOWER(c.lastName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
			"LOWER(c.address) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
			"LOWER(c.email) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
			"LOWER(c.phone) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
			"LOWER(c.ifu) LIKE LOWER(CONCAT('%', :term, '%'))")
	Page<Customer> search(@Param("term") String term, Pageable pageable);
}


