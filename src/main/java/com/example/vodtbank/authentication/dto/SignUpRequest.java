package com.example.vodtbank.authentication.dto;

import java.util.List;

import com.example.vodtbank.role.dto.RoleDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
		@NotBlank(message = "First name is required")
		String firstName,

		String lastName,

		@NotBlank(message = "Email is required")
		@Email
		String email,

		String phoneNumber,

		@NotBlank(message = "Password is required")
		String password,

		List<RoleDto> roles
) {}
