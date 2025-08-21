package com.example.demoQrcode.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "customers", uniqueConstraints = {
		@UniqueConstraint(columnNames = {"ifu"}),
		@UniqueConstraint(columnNames = {"email"})
})
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Le nom est obligatoire")
	@Column(nullable = false)
	private String lastName;

	@NotBlank(message = "Le prénom est obligatoire")
	@Column(nullable = false)
	private String firstName;

	@NotBlank(message = "L'adresse est obligatoire")
	@Column(nullable = false)
	private String address;

	@NotBlank(message = "L'IFU est obligatoire")
	@Column(nullable = false, length = 64)
	private String ifu;

	@NotBlank(message = "Le téléphone est obligatoire")
	@Column(nullable = false, length = 32)
	private String phone;

	@Email(message = "E-mail invalide")
	@NotBlank(message = "L'e-mail est obligatoire")
	@Column(nullable = false)
	private String email;

	public Customer() {}

	public Customer(String lastName, String firstName, String address, String ifu, String phone, String email) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.address = address;
		this.ifu = ifu;
		this.phone = phone;
		this.email = email;
	}

	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }

	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }

	public String getIfu() { return ifu; }
	public void setIfu(String ifu) { this.ifu = ifu; }

	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
}


