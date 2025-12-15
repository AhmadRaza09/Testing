package com.example.demo.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookRepositoryTest {

	@Container
	static MariaDBContainer<?> mariadb = new MariaDBContainer<>("mariadb:11.4")
			.withDatabaseName("testdb")
			.withUsername("test")
			.withPassword("test");

	@DynamicPropertySource
	static void props(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mariadb::getJdbcUrl);
		registry.add("spring.datasource.username", mariadb::getUsername);
		registry.add("spring.datasource.password", mariadb::getPassword);
		registry.add("spring.datasource.driver-class-name", () -> "org.mariadb.jdbc.Driver");
	}

	@Autowired
	BookRepository repo;

	@Test
	void saveAndFindByIsbn() {
		repo.save(new Book("isbn-123", "Testcontainers Intro"));

		assertThat(repo.findByIsbn("isbn-123"))
				.isPresent()
				.get()
				.extracting(Book::getTitle)
				.isEqualTo("Testcontainers Intro");
	}

	@Test
	void uniqueIsbn_enforcedAndUpdatable() {
		repo.save(new Book("dup", "First"));
		repo.save(new Book("unique", "Second"));

		var second = repo.findByIsbn("unique").orElseThrow();
		second.setTitle("Updated");
		repo.save(second);

		assertThat(repo.findAll()).hasSize(2);
		assertThat(repo.findByIsbn("unique")).get().extracting(Book::getTitle).isEqualTo("Updated");
	}
}

