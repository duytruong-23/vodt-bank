package com.example.vodtbank.authentication.controller;

import com.example.vodtbank.authentication.dto.ResetPasswordRequest;
import com.example.vodtbank.authentication.dto.SignInRequest;
import com.example.vodtbank.authentication.dto.SignInResponse;
import com.example.vodtbank.authentication.dto.SignUpRequest;
import com.example.vodtbank.authentication.dto.UserDto;
import com.example.vodtbank.authentication.service.AuthService;
import com.example.vodtbank.response.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<Response<UserDto>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
		UserDto userDto = authService.signUp(signUpRequest);

		return ResponseEntity.ok(Response.success("User registered successfully", userDto));
	}

	@PostMapping("/signin")
	public ResponseEntity<Response<SignInResponse>> signIn(@RequestBody @Valid SignInRequest signInRequest) {
		SignInResponse signInResponse = authService.signIn(signInRequest);

		return ResponseEntity.ok(Response.success("User signed in successfully", signInResponse));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<Response<String>> forgotPassword(@RequestBody String email) {
		authService.forgotPassword(email);

		return ResponseEntity.ok(Response.noContent("Password reset code sent to email"));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<Response<String>> resetPassword(
			@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
		authService.updatePasswordViaResetCode(resetPasswordRequest);

		return ResponseEntity.ok(Response.noContent("Password reset successfully"));
	}
}
