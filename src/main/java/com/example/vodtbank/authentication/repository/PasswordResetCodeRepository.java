package com.example.vodtbank.authentication.repository;

import java.util.Optional;

import com.example.vodtbank.authentication.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
	Optional<PasswordResetCode> findByCode(String code);

	void deleteByUserId(Long userId);

	boolean existsPasswordResetCodeByCode(String code);
}
