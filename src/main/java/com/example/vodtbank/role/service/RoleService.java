package com.example.vodtbank.role.service;

import java.util.List;

import com.example.vodtbank.role.dto.RoleDto;

public interface RoleService {
	RoleDto createRole(RoleDto roleDto);

	RoleDto updateRole(RoleDto roleDto);

	List<RoleDto> getAllRoles();

	void deleteRole(Long roleId);

	Long getDefaultRoleId();
}
