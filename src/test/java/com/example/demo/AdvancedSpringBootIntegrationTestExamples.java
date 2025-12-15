package com.example.demo;

import com.example.demo.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ====================================================================================
 * ADVANCED @SpringBootTest EXAMPLES
 * ====================================================================================
 * 
 * This file demonstrates advanced integration testing scenarios with @SpringBootTest.
 */

// ====================================================================================
// EXAMPLE 1: Testing with Active Profiles
// ====================================================================================
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProfileBasedIntegrationTest {

	/**
	 * @ActiveProfiles specifies which Spring profiles to activate.
	 * 
	 * This is useful for:
	 * - Using test-specific configurations
	 * - Activating test profiles that have different beans
	 * - Testing profile-specific behavior
	 * 
	 * You would typically have:
	 * - application.properties (default)
	 * - application-test.properties (test profile)
	 * - application-prod.properties (production profile)
	 */

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private GreetingService greetingService;

	@Test
	void testWithTestProfile() {
		// The test profile is active, so any profile-specific configuration is applied
		String result = greetingService.getGreeting("ProfileTest");
		assertThat(result).isEqualTo("Hello, ProfileTest!");
	}
}


// ====================================================================================
// EXAMPLE 2: Custom Test Configuration with @TestConfiguration
// ====================================================================================
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomTestConfigurationExample {

	/**
	 * @TestConfiguration allows you to define additional beans or override existing
	 * ones for testing purposes only. These beans are added to the context but
	 * don't replace the main application configuration.
	 */

	@TestConfiguration
	static class TestConfig {
		/**
		 * This bean is added to the test context.
		 * You could override an existing bean by making it @Primary
		 */
		@Bean
		@Primary // This makes it take precedence over the real bean
		public GreetingService testGreetingService() {
			return new GreetingService() {
				@Override
				public String getGreeting(String name) {
					// Custom test implementation
					return "TEST: Hello, " + (name != null ? name : "friend") + "!";
				}
			};
		}
	}

	@Autowired
	private GreetingService greetingService;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void testWithCustomBean() {
		// This will use the test implementation from TestConfig
		String result = greetingService.getGreeting("Custom");
		assertThat(result).isEqualTo("TEST: Hello, Custom!");
	}

	@Test
	void testWithCustomBeanViaHttp() {
		// Even HTTP requests use the test bean
		String url = "http://localhost:" + port + "/api/greeting?name=HTTP";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("TEST: Hello, HTTP!");
	}
}


// ====================================================================================
// EXAMPLE 3: Context Isolation with @DirtiesContext
// ====================================================================================
@SpringBootTest
class ContextIsolationExample {

	/**
	 * @DirtiesContext marks that a test modifies the Spring context.
	 * This forces Spring to reload the context after this test.
	 * 
	 * Use when:
	 * - Test modifies shared state
	 * - Test changes configuration
	 * - Test needs a fresh context for isolation
	 */

	@Autowired
	private GreetingService greetingService;

	@Test
	void testOne() {
		// This test doesn't modify context
		assertThat(greetingService.getGreeting("Test1")).isNotNull();
	}

	@DirtiesContext
	@Test
	void testThatModifiesContext() {
		/**
		 * After this test, Spring will reload the context.
		 * This ensures test isolation.
		 * 
		 * Note: This makes tests slower because context is reloaded!
		 */
		assertThat(greetingService.getGreeting("Test2")).isNotNull();
	}

	@Test
	void testAfterDirtiesContext() {
		// Context was reloaded, so this test gets a fresh context
		assertThat(greetingService.getGreeting("Test3")).isNotNull();
	}
}


// ====================================================================================
// EXAMPLE 4: Testing Multiple Web Environments
// ====================================================================================
class WebEnvironmentExamples {

	/**
	 * SpringBootTest.WebEnvironment options:
	 * 
	 * 1. MOCK (default)
	 *    - No real HTTP server
	 *    - Uses MockMvc for testing
	 *    - Fastest option
	 */
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
	static class MockWebEnvironmentTest {
		@Test
		void testWithMockEnvironment() {
			// No real server, use MockMvc if testing web layer
		}
	}

	/**
	 * 2. RANDOM_PORT
	 *    - Real embedded server on random port
	 *    - Use TestRestTemplate for HTTP calls
	 *    - Good for integration testing
	 */
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
	static class RandomPortTest {
		@LocalServerPort
		private int port;

		@Autowired
		private TestRestTemplate restTemplate;

		@Test
		void testWithRealServer() {
			String url = "http://localhost:" + port + "/api/greeting";
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		}
	}

	/**
	 * 3. DEFINED_PORT
	 *    - Real server on port from application.properties
	 *    - Useful when testing with specific port configurations
	 */
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
	static class DefinedPortTest {
		// Server runs on configured port (e.g., server.port=8080)
	}

	/**
	 * 4. NONE
	 *    - No web environment at all
	 *    - For non-web applications or service layer testing only
	 */
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
	static class NoWebEnvironmentTest {
		@Autowired
		private GreetingService greetingService;

		@Test
		void testWithoutWebLayer() {
			// Only testing service layer, no web server needed
			assertThat(greetingService.getGreeting("Test")).isNotNull();
		}
	}
}


// ====================================================================================
// EXAMPLE 5: Custom RestTemplate Configuration
// ====================================================================================
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomRestTemplateExample {

	/**
	 * You can customize TestRestTemplate for your tests.
	 * Useful for:
	 * - Adding authentication headers
	 * - Setting timeouts
	 * - Adding interceptors
	 */

	@LocalServerPort
	private int port;

	@TestConfiguration
	static class RestTemplateConfig {
		@Bean
		public RestTemplateBuilder restTemplateBuilder() {
			return new RestTemplateBuilder()
					.rootUri("http://localhost")
					// Add customizations here
					// .basicAuthentication("user", "pass")
					// .setConnectTimeout(Duration.ofSeconds(5))
					;
		}
	}

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void testWithCustomRestTemplate() {
		// RestTemplate is customized with rootUri
		String url = ":" + port + "/api/greeting";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}


// ====================================================================================
// EXAMPLE 6: Testing Application Startup
// ====================================================================================
@SpringBootTest
class ApplicationStartupTest {

	/**
	 * Sometimes you just need to verify the application starts correctly.
	 * This is especially useful for:
	 * - Configuration validation
	 * - Bean creation verification
	 * - Auto-configuration testing
	 */

	@Autowired
	private com.example.demo.DemoApplication application;

	@Test
	void applicationContextLoadsSuccessfully() {
		/**
		 * This test passes only if:
		 * - All beans can be created
		 * - All dependencies are satisfied
		 * - Configuration is valid
		 * - No startup errors occur
		 * 
		 * If any of these fail, the test fails during context loading,
		 * which is exactly what we want to catch!
		 */
		assertThat(application).isNotNull();
	}
}


// ====================================================================================
// SUMMARY: When to Use Each Approach
// ====================================================================================

/*
 * CHOOSE @SpringBootTest WHEN:
 * 
 * ✅ Testing End-to-End Flows
 *    - User request → Controller → Service → Repository → Database → Response
 * 
 * ✅ Testing Configuration
 *    - Auto-configuration works correctly
 *    - Properties are loaded properly
 *    - Profiles work as expected
 * 
 * ✅ Testing Security
 *    - Authentication/authorization works
 *    - Security filters are applied
 *    - Endpoints are protected correctly
 * 
 * ✅ Testing Integrations
 *    - External services integration
 *    - Message queues
 *    - Database transactions
 * 
 * ✅ Testing Application Startup
 *    - Context loads without errors
 *    - All beans are created
 *    - Configuration is valid
 * 
 * 
 * AVOID @SpringBootTest WHEN:
 * 
 * ❌ Testing Single Classes
 *    - Use unit tests with mocks
 * 
 * ❌ Testing Only Controllers
 *    - Use @WebMvcTest (faster)
 * 
 * ❌ Testing Only Repositories
 *    - Use @DataJpaTest (faster)
 * 
 * ❌ Performance-Critical Tests
 *    - Use slice tests or unit tests
 */

