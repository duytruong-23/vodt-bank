package com.example.vodtbank.authentication.controller;

import java.util.List;

import com.example.vodtbank.authentication.dto.UpdatePasswordRequest;
import com.example.vodtbank.authentication.dto.UserDto;
import com.example.vodtbank.authentication.service.UserService;
import com.example.vodtbank.response.Response;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority(T(com.example.vodtbank.role.dto.SystemRole).ADMIN.name())")
	public ResponseEntity<Response<List<UserDto>>> getAllUsers(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "50") int size
	) {
		List<UserDto> users = userService.getUsers(page, size).getContent();
		return ResponseEntity.ok(Response.success("Users retrieved successfully", users));
	}

	@GetMapping("/me")
	public ResponseEntity<Response<UserDto>> getCurrentUser() {
		UserDto userDto = userService.getCurrentUser();
		return ResponseEntity.ok(Response.success("Current user retrieved successfully", userDto));
	}

	@PutMapping("/update-password")
	public ResponseEntity<Response<Void>> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
		userService.updatePassword(request);
		return ResponseEntity.ok(Response.noContent("Password updated successfully"));
	}

	@PutMapping("/profile-picture")
	public ResponseEntity<Response<String>> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
		String fileUrl = userService.uploadProfilePicture(file);
		return ResponseEntity.ok(Response.success("Profile picture uploaded successfully", fileUrl));
	}
}
