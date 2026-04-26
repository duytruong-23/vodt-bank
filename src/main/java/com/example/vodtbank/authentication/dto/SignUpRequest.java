package com.example.vodtbank.authentication.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
		@NotBlank(message = "First name is required")
		String firstName,

		String lastName,

		@NotBlank(message = "Email is required")
		@Email
		String email,

		@NotBlank(message = "Phone number is required")
		String phoneNumber,

		@NotBlank(message = "Password is required")
		String password,

		List<String> roles
) {}
