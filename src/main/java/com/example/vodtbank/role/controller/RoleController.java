package com.example.vodtbank.role.controller;

import java.util.List;

import com.example.vodtbank.response.Response;
import com.example.vodtbank.role.dto.RoleDto;
import com.example.vodtbank.role.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {
	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@PostMapping
	public ResponseEntity<Response<RoleDto>> createRole(@RequestBody RoleDto roleDto) {
		RoleDto role = roleService.createRole(roleDto);
		Response<RoleDto> response = new Response<>(HttpStatus.OK.value(), "Role saved successfully", role);

		return ResponseEntity.ok(response);
	}

	@PutMapping
	public ResponseEntity<Response<RoleDto>> updateRole(@RequestBody RoleDto roleDto) {
		RoleDto role = roleService.updateRole(roleDto);
		Response<RoleDto> response = new Response<>(HttpStatus.OK.value(), "Role updated successfully", role);

		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Response<List<RoleDto>>> getAllRoles() {
		List<RoleDto> roles = roleService.getAllRoles();
		Response<List<RoleDto>> response = new Response<>(HttpStatus.OK.value(), "Roles retrieved successfully", roles);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Response<Void>> deleteRole(@PathVariable Long id) {
		roleService.deleteRole(id);
		Response<Void> response = new Response<>(HttpStatus.OK.value(), "Role deleted successfully", null);

		return ResponseEntity.ok(response);
	}

}
