package com.example.demo;

import com.example.demo.service.GreetingService;
import com.example.demo.web.GreetingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ====================================================================================
 * SPRING BOOT INTEGRATION TESTS WITH @SpringBootTest - COMPREHENSIVE GUIDE
 * ====================================================================================
 * 
 * WHAT IS @SpringBootTest?
 * ------------------------
 * @SpringBootTest is the main annotation for integration testing in Spring Boot.
 * It loads the FULL Spring Application Context, just like when your application
 * runs in production. This is different from:
 * 
 * - @WebMvcTest: Only loads web layer (controllers) - SLICE TEST
 * - @DataJpaTest: Only loads JPA repositories - SLICE TEST  
 * - @JsonTest: Only loads JSON serialization - SLICE TEST
 * 
 * @SpringBootTest loads EVERYTHING: controllers, services, repositories, 
 * configurations, security, etc. This makes it slower but more realistic.
 * 
 * 
 * FULL CONTEXT TESTING
 * --------------------
 * Full context testing means testing your application as a whole system:
 * - All beans are created and wired together (dependency injection)
 * - Configuration properties are loaded
 * - Spring Boot auto-configuration is applied
 * - The application runs as close to production as possible
 * 
 * 
 * WHEN TO USE @SpringBootTest?
 * -----------------------------
 * ✅ Testing multiple layers together (controller + service + repository)
 * ✅ Testing configuration and auto-configuration
 * ✅ Testing security configuration end-to-end
 * ✅ Testing application context loading
 * ✅ Integration testing with embedded servers
 * ✅ Testing with real database connections
 * 
 * ❌ DON'T use for:
 *   - Unit testing single components (use @ExtendWith(MockitoExtension.class))
 *   - Testing only controllers (use @WebMvcTest - faster)
 *   - Testing only JPA repositories (use @DataJpaTest - faster)
 */

// ====================================================================================
// EXAMPLE 1: BASIC @SpringBootTest - Context Loading Test
// ====================================================================================
@SpringBootTest
class BasicSpringBootTestExample {

	/**
	 * The most basic use: Verify that the Spring Application Context loads successfully.
	 * 
	 * This test passes if:
	 * - All @Component, @Service, @Repository, @Controller beans can be created
	 * - All dependencies can be injected
	 * - Configuration is valid
	 * - No circular dependencies or missing beans
	 */
	@Test
	void contextLoads() {
		// If we reach here, the context loaded successfully!
		// This is the simplest integration test - just checking context startup
	}

	/**
	 * Verify that specific beans exist in the context.
	 * This proves that your components are properly registered.
	 */
	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void greetingServiceBeanExists() {
		// Verify that GreetingService bean is in the context
		assertThat(applicationContext.getBean(GreetingService.class)).isNotNull();
	}

	@Test
	void greetingControllerBeanExists() {
		// Verify that GreetingController bean is in the context
		assertThat(applicationContext.getBean(GreetingController.class)).isNotNull();
	}
}


// ====================================================================================
// EXAMPLE 2: FULL CONTEXT TESTING - Testing Real Bean Interactions
// ====================================================================================
@SpringBootTest
class FullContextIntegrationTest {

	/**
	 * @Autowired works because the FULL Spring context is loaded.
	 * GreetingService is a REAL bean, not a mock!
	 * 
	 * This tests the actual business logic with real dependencies.
	 */
	@Autowired
	private GreetingService greetingService;

	@Test
	void serviceWorksWithRealImplementation() {
		// This is testing the ACTUAL GreetingService implementation
		String result = greetingService.getGreeting("Alice");
		
		assertThat(result).isEqualTo("Hello, Alice!");
	}

	@Test
	void serviceHandlesNullName() {
		// Testing real business logic
		String result = greetingService.getGreeting(null);
		
		assertThat(result).isEqualTo("Hello, friend!");
	}

	@Test
	void serviceTrimsWhitespace() {
		// Testing real implementation details
		String result = greetingService.getGreeting("  Bob  ");
		
		assertThat(result).isEqualTo("Hello, Bob!");
	}
}


// ====================================================================================
// EXAMPLE 3: FULL CONTEXT WITH WEB LAYER - Real HTTP Testing
// ====================================================================================
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FullContextWithWebServerTest {

	/**
	 * webEnvironment = RANDOM_PORT starts a REAL embedded web server
	 * on a random available port. This tests the full HTTP stack!
	 * 
	 * Options:
	 * - WebEnvironment.MOCK: Default, uses MockMvc (no real server)
	 * - WebEnvironment.RANDOM_PORT: Real server on random port (THIS EXAMPLE)
	 * - WebEnvironment.DEFINED_PORT: Real server on port from application.properties
	 * - WebEnvironment.NONE: No web environment (for non-web apps)
	 */

	@LocalServerPort
	private int port; // Injected with the random port number

	@Autowired
	private TestRestTemplate restTemplate; // Provided by Spring Boot Test

	@Test
	void greetingEndpointWorksWithFullContext() {
		// This makes a REAL HTTP request to a REAL server
		String url = "http://localhost:" + port + "/api/greeting?name=Charlie";
		
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		// Verify HTTP response
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Hello, Charlie!");
	}

	@Test
	void greetingEndpointWithNoName() {
		String url = "http://localhost:" + port + "/api/greeting";
		
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Hello, friend!");
	}

	/**
	 * This test proves FULL INTEGRATION:
	 * - HTTP request comes in
	 * - Spring MVC dispatches to controller
	 * - Controller calls service
	 * - Service returns result
	 * - Response goes back over HTTP
	 * 
	 * ALL layers are tested together with real implementations!
	 */
}


// ====================================================================================
// EXAMPLE 4: TESTING WITH CUSTOM TEST PROPERTIES
// ====================================================================================
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
		"spring.application.name=test-app",
		"logging.level.com.example.demo=DEBUG"
})
class CustomPropertiesIntegrationTest {

	/**
	 * @TestPropertySource allows you to override properties for testing.
	 * This is useful for:
	 * - Using test database configurations
	 * - Changing logging levels
	 * - Overriding feature flags
	 * - Testing different profiles
	 */

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private ApplicationContext context;

	@Test
	void testWithCustomProperties() {
		// Properties from @TestPropertySource are applied
		// You can verify them or test behavior that depends on them
		String url = "http://localhost:" + port + "/api/greeting";
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}


// ====================================================================================
// EXAMPLE 5: MULTI-LAYER INTEGRATION TEST
// ====================================================================================
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MultiLayerIntegrationTest {

	/**
	 * This demonstrates TRUE integration testing:
	 * Testing multiple layers working together.
	 * 
	 * Flow being tested:
	 * HTTP Request → Controller → Service → Business Logic → Response
	 */

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private GreetingService greetingService;

	@Autowired
	private GreetingController greetingController;

	@Test
	void testFullStackIntegration() {
		// 1. Make HTTP request (tests web layer)
		String url = "http://localhost:" + port + "/api/greeting?name=Integration";
		ResponseEntity<String> httpResponse = restTemplate.getForEntity(url, String.class);

		// 2. Verify HTTP response
		assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(httpResponse.getBody()).isEqualTo("Hello, Integration!");

		// 3. Verify service layer directly (same bean used by controller)
		String serviceResult = greetingService.getGreeting("Integration");
		assertThat(serviceResult).isEqualTo(httpResponse.getBody());

		// 4. Verify controller directly (can also test this way)
		String controllerResult = greetingController.greet("Integration");
		assertThat(controllerResult).isEqualTo("Hello, Integration!");
	}

	/**
	 * Key Point: All three approaches (HTTP, service, controller) use the SAME beans.
	 * This proves the full integration works correctly!
	 */
}


// ====================================================================================
// EXAMPLE 6: CONTEXT CACHING - Understanding Test Performance
// ====================================================================================
@SpringBootTest
class ContextCachingExample {

	/**
	 * SPRING CONTEXT CACHING:
	 * 
	 * Spring Boot caches the application context between tests if:
	 * - Same @SpringBootTest configuration
	 * - Same active profiles
	 * - Same configuration classes
	 * 
	 * This makes subsequent tests faster because context is reused!
	 * 
	 * Look at test output - you'll see context started once, then reused.
	 */

	@Autowired
	private GreetingService greetingService;

	@Test
	void testOne() {
		// Context loads here (if first test with this configuration)
		assertThat(greetingService).isNotNull();
	}

	@Test
	void testTwo() {
		// Context is REUSED from testOne (much faster!)
		assertThat(greetingService).isNotNull();
	}

	@Test
	void testThree() {
		// Context is STILL REUSED (even faster!)
		assertThat(greetingService).isNotNull();
	}
}


// ====================================================================================
// KEY DIFFERENCES: @SpringBootTest vs @WebMvcTest vs Unit Tests
// ====================================================================================

/*
 * ┌─────────────────────┬──────────────────┬─────────────────┬──────────────────┐
 * │ Feature             │ @SpringBootTest  │ @WebMvcTest     │ Unit Test        │
 * ├─────────────────────┼──────────────────┼─────────────────┼──────────────────┤
 * │ Context Type        │ Full Application │ Web Layer Only  │ No Context       │
 * │ Beans Loaded        │ All (@Service,   │ Controllers     │ None (manual)    │
 * │                     │  @Repository,    │  only           │                  │
 * │                     │  @Controller)    │                 │                  │
 * ├─────────────────────┼──────────────────┼─────────────────┼──────────────────┤
 * │ Speed               │ Slower           │ Fast            │ Fastest          │
 * │ Use Case            │ Integration      │ Controller      │ Single class     │
 * │                     │  Testing         │  Testing        │  testing         │
 * ├─────────────────────┼──────────────────┼─────────────────┼──────────────────┤
 * │ Real HTTP Server    │ Yes (optional)   │ No (MockMvc)    │ N/A              │
 * │ Database            │ Real/Embedded    │ No              │ Mock             │
 * │ Dependencies        │ Real             │ Mocked          │ Mocked           │
 * └─────────────────────┴──────────────────┴─────────────────┴──────────────────┘
 * 
 * 
 * EXAMPLE SCENARIOS:
 * 
 * Use @SpringBootTest when:
 * - Testing: Controller → Service → Repository → Database
 * - Testing security configuration end-to-end
 * - Testing application startup and configuration
 * - Testing with real embedded databases
 * - Testing scheduled tasks
 * 
 * Use @WebMvcTest when:
 * - Testing only controller endpoints
 * - Testing request/response mapping
 * - Testing validation
 * - Don't need services/repositories
 * 
 * Use Unit Tests when:
 * - Testing business logic in isolation
 * - Testing utility methods
 * - Testing calculations/transformations
 * - Don't need Spring features
 */


// ====================================================================================
// BEST PRACTICES
// ====================================================================================

/*
 * 1. USE APPROPRIATE TEST SLICES
 *    - Don't use @SpringBootTest for everything
 *    - Use @WebMvcTest for controller-only tests
 *    - Use @DataJpaTest for repository tests
 *    - Use @SpringBootTest only when you need full integration
 * 
 * 2. OPTIMIZE CONTEXT LOADING
 *    - Use @TestConfiguration to customize test beans
 *    - Use @MockBean sparingly (defeats purpose of integration test)
 *    - Leverage context caching by grouping similar tests
 * 
 * 3. MANAGE TEST DATA
 *    - Use @Sql for database test data
 *    - Use @Transactional for test isolation
 *    - Clean up after tests with @DirtiesContext if needed
 * 
 * 4. ISOLATION
 *    - Each test should be independent
 *    - Use @DirtiesContext if test modifies shared state
 *    - Use separate test profiles when needed
 * 
 * 5. PERFORMANCE
 *    - Group tests with same configuration together
 *    - Use test slices (@WebMvcTest) when possible
 *    - Consider using @TestInstance(Lifecycle.PER_CLASS) for expensive setup
 */

