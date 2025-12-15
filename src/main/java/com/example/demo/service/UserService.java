package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * UserService - Business logic layer for User operations
 * 
 * This service uses the UserRepository to interact with the database.
 * It contains business logic and validation rules.
 */
@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;

	// Constructor injection (recommended in Spring)
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Create a new user
	 * @param user User entity to save
	 * @return Saved user with generated ID
	 */
	public User createUser(User user) {
		// Business logic: Validate email uniqueness
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
		}
		
		// Business logic: Validate age
		if (user.getAge() < 0) {
			throw new IllegalArgumentException("Age cannot be negative");
		}
		
		return userRepository.save(user);
	}

	/**
	 * Get user by ID
	 * @param id User ID
	 * @return Optional containing user if found
	 */
	@Transactional(readOnly = true)
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	/**
	 * Get user by email
	 * @param email User email
	 * @return Optional containing user if found
	 */
	@Transactional(readOnly = true)
	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * Get all users
	 * @return List of all users
	 */
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	/**
	 * Update an existing user
	 * @param id User ID
	 * @param user Updated user data
	 * @return Updated user
	 */
	public User updateUser(Long id, User user) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
		
		// Update fields (business logic)
		if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
			// Check if new email already exists
			if (userRepository.findByEmail(user.getEmail()).isPresent()) {
				throw new IllegalArgumentException("Email " + user.getEmail() + " already exists");
			}
			existingUser.setEmail(user.getEmail());
		}
		
		if (user.getName() != null) {
			existingUser.setName(user.getName());
		}
		
		if (user.getAge() != null) {
			if (user.getAge() < 0) {
				throw new IllegalArgumentException("Age cannot be negative");
			}
			existingUser.setAge(user.getAge());
		}
		
		return userRepository.save(existingUser);
	}

	/**
	 * Delete user by ID
	 * @param id User ID
	 */
	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new IllegalArgumentException("User with id " + id + " not found");
		}
		userRepository.deleteById(id);
	}
}


