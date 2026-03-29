package com.example.vodtbank.authentication.dto;

import java.util.List;

import com.example.vodtbank.role.dto.RoleDto;

public record SignInResponse(
		String accessToken,
		List<RoleDto> roles
) {}
