package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {

	public String getGreeting(String name) {
		String target = (name == null || name.isBlank()) ? "friend" : name.trim();
		return "Hello, " + target + "!";
	}
}

