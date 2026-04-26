package com.example.vodtbank.authentication.service;

import com.example.vodtbank.authentication.dto.PasswordResetCodeDto;

public interface PasswordResetCodeService {
	String generateCode();

	PasswordResetCodeDto replaceCodeForUser(Long userId);

	boolean validateCode(String code);
}
