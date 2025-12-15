package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * User Entity - JPA Entity representing a User in the database
 * 
 * This entity will be mapped to a "users" table in the database.
 * Spring Data JPA will automatically create the table based on this entity.
 */
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer age;

	// Default constructor (required by JPA)
	public User() {
	}

	// Constructor with fields
	public User(String email, String name, Integer age) {
		this.email = email;
		this.name = name;
		this.age = age;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", email='" + email + '\'' +
				", name='" + name + '\'' +
				", age=" + age +
				'}';
	}
}


