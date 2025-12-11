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

@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerFullContextMockMvcTest {

	@Autowired
	MockMvc mockMvc;

	@Test
	void greetWithRealService_returnsDefault() throws Exception {
		mockMvc.perform(get("/api/greeting"))
				.andExpect(status().isOk())
				.andExpect(content().string("Hello, friend!"));
	}
}

