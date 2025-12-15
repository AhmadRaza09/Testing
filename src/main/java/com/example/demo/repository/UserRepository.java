package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Spring Data JPA Repository Interface
 * 
 * This interface extends JpaRepository, which provides CRUD operations
 * out of the box. Spring Data JPA automatically implements this interface
 * at runtime.
 * 
 * Available methods (from JpaRepository):
 * - save(User entity) - Save or update a user
 * - findById(Long id) - Find user by ID
 * - findAll() - Find all users
 * - deleteById(Long id) - Delete user by ID
 * - count() - Count total users
 * - existsById(Long id) - Check if user exists
 * 
 * Custom query methods (Spring Data JPA will implement automatically):
 * - findByEmail(String email) - Find user by email
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Custom query method - Spring Data JPA automatically implements this
	 * based on the method name following the convention: findBy{FieldName}
	 * 
	 * This will generate: SELECT * FROM users WHERE email = ?
	 */
	Optional<User> findByEmail(String email);
}


