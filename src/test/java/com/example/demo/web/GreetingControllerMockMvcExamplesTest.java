package com.example.demo.web;

import com.example.demo.service.GreetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Concrete MockMvc examples that mirror the teaching notes:
 * - @WebMvcTest slice
 * - standaloneSetup (no Spring context)
 * - full context with @SpringBootTest
 */

@WebMvcTest(GreetingController.class)
class GreetingControllerWebSliceMockMvcTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	GreetingService greetingService;

	@Test
	void greetWithName_returnsOkAndBody() throws Exception {
		given(greetingService.getGreeting("Dana")).willReturn("Hello, Dana!");

		mockMvc.perform(get("/api/greeting").param("name", "Dana"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
				.andExpect(content().string("Hello, Dana!"));
	}
}

class GreetingControllerStandaloneMockMvcTest {

	MockMvc mockMvc;
	GreetingService greetingService;

	@BeforeEach
	void setUp() {
		greetingService = mock(GreetingService.class);
		mockMvc = MockMvcBuilders
				.standaloneSetup(new GreetingController(greetingService))
				.build();
	}

	@Test
	void greetWithoutName_returnsDefault() throws Exception {
		given(greetingService.getGreeting(null)).willReturn("Hello, friend!");

		mockMvc.perform(get("/api/greeting"))
				.andExpect(status().isOk())
				.andExpect(content().string("Hello, friend!"));
	}
}

/**
 * ====================================================================================
 * FULL CONTEXT TESTING WITH @SpringBootTest + @AutoConfigureMockMvc
 * ====================================================================================
 * 
 * KEY DIFFERENCES FROM @WebMvcTest:
 * 1. FULL Spring context is loaded (all beans, services, repositories, etc.)
 * 2. REAL GreetingService bean is used (NOT mocked with @MockBean)
 * 3. All layers of the application are active
 * 4. Slower but more realistic - tests the actual integration
 * 
 * WHEN TO USE:
 * - When you want to test the full integration (Controller + Service together)
 * - When you want to verify real bean interactions
 * - When testing configuration and auto-configuration
 * - When you need the full application context for your test
 */
@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerFullContextMockMvcTest {

	@Autowired
	MockMvc mockMvc;

	/**
	 * NOTE: No @MockBean for GreetingService!
	 * The REAL GreetingService bean from the application context is used.
	 * This tests the actual integration between Controller and Service.
	 */

	@Test
	void greetWithRealService_returnsDefault() throws Exception {
		// This tests with the REAL GreetingService implementation
		// No mocking - actual business logic is executed
		mockMvc.perform(get("/api/greeting"))
				.andExpect(status().isOk())
				.andExpect(content().string("Hello, friend!"));
	}

	@Test
	void greetWithName_usesRealServiceImplementation() throws Exception {
		// Tests the full flow: HTTP Request → Controller → Real Service → Response
		mockMvc.perform(get("/api/greeting").param("name", "Integration"))
				.andExpect(status().isOk())
				.andExpect(content().string("Hello, Integration!"));
	}

	@Test
	void greetWithWhitespace_trimsUsingRealService() throws Exception {
		// Verifies real service logic (trimming whitespace)
		mockMvc.perform(get("/api/greeting").param("name", "  Test  "))
				.andExpect(status().isOk())
				.andExpect(content().string("Hello, Test!"));
	}

	/**
	 * COMPARISON:
	 * 
	 * @WebMvcTest (line 28):
	 *   - Loads only web layer (controllers)
	 *   - GreetingService is MOCKED (@MockBean)
	 *   - Faster execution
	 *   - Tests only controller logic
	 * 
	 * @SpringBootTest (this class):
	 *   - Loads FULL application context
	 *   - GreetingService is REAL bean
	 *   - Slower execution (context loading)
	 *   - Tests controller + service integration
	 */
}

