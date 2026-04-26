package com.example.vodtbank.authentication.service.impl;

import java.security.SecureRandom;

import com.example.vodtbank.authentication.repository.PasswordResetCodeRepository;
import com.example.vodtbank.authentication.service.PasswordResetCodeService;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetCodeImpl implements PasswordResetCodeService {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int CODE_LENGTH = 6;

	private final PasswordResetCodeRepository passwordResetCodeRepository;

	public PasswordResetCodeImpl(PasswordResetCodeRepository passwordResetCodeRepository) {
		this.passwordResetCodeRepository = passwordResetCodeRepository;
	}

	@Override
	public String generateCode() {
		String code;
		do {
			code = generateRandomCode();
		} while(passwordResetCodeRepository.findByCode(code).isPresent());

		return code;
	}

	private String generateRandomCode() {
		StringBuilder code = new StringBuilder();
		SecureRandom random = new SecureRandom();

		for (int i = 0; i < CODE_LENGTH; i++) {
			int index = random.nextInt(CHARACTERS.length());
			code.append(CHARACTERS.charAt(index));
		}

		return code.toString();
	}
}
