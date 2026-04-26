package com.example.vodtbank.role.dto;

public enum SystemRole {
	CUSTOMER,
	ADMIN,
	AUDITOR;

	public static boolean isSystemRole(String role) {
		for (SystemRole systemRole : SystemRole.values()) {
			if (systemRole.name().equalsIgnoreCase(role)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAdminRole(String role) {
		return ADMIN.name().equalsIgnoreCase(role);
	}
}
