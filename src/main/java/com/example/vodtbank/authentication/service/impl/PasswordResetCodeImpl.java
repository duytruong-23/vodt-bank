package com.example.vodtbank.authentication.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import com.example.vodtbank.authentication.dto.PasswordResetCodeDto;
import com.example.vodtbank.authentication.entity.PasswordResetCode;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.PasswordResetCodeRepository;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.authentication.service.PasswordResetCodeService;
import com.example.vodtbank.exception.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetCodeImpl implements PasswordResetCodeService {
	private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int CODE_LENGTH = 6;
	private static final int EXPIRATION_MINUTES = 15;

	private final PasswordResetCodeRepository passwordResetCodeRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;

	public PasswordResetCodeImpl(PasswordResetCodeRepository passwordResetCodeRepository, UserRepository userRepository,
			ModelMapper modelMapper) {
		this.passwordResetCodeRepository = passwordResetCodeRepository;
		this.userRepository = userRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public String generateCode() {
		String code;
		do {
			code = generateRandomCode();
		} while(passwordResetCodeRepository.findByCode(code).isPresent());

		return code;
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public PasswordResetCodeDto replaceCodeForUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
		passwordResetCodeRepository.deleteByUserId(user.getId());

		String newCode = generateCode();
		PasswordResetCode passwordResetCode = new PasswordResetCode();
		passwordResetCode.setUser(user);
		passwordResetCode.setCode(newCode);
		passwordResetCode.setExpirationDate(calculateExpirationDate());

		return modelMapper.map(passwordResetCodeRepository.save(passwordResetCode), PasswordResetCodeDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean validateCode(String code) {
		PasswordResetCode passwordResetCode = passwordResetCodeRepository.findByCode(code)
				.orElseThrow(() -> new NotFoundException("Invalid password reset code"));

		return passwordResetCode.getExpirationDate().isBefore(LocalDateTime.now()) && !passwordResetCode.isUsed();
	}

	private LocalDateTime calculateExpirationDate() {
		return LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
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
