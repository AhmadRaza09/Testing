package com.example.demo.web;

import com.example.demo.service.GreetingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GreetingControllerTest {

	@Mock
	private GreetingService greetingService;

	@InjectMocks
	private GreetingController greetingController;

	@Test
	void greetReturnsServiceResult() {
		String name = "Dana";
		given(greetingService.getGreeting(name)).willReturn("Hello, Dana!");

		String result = greetingController.greet(name);

		assertThat(result).isEqualTo("Hello, Dana!");
	}
}

