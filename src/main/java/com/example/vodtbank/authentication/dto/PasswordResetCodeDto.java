package com.example.vodtbank.authentication.dto;

import java.time.LocalDateTime;

import com.example.vodtbank.common.dto.BaseDto;

public class PasswordResetCodeDto extends BaseDto {
	private String code;
	private LocalDateTime expirationDate;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LocalDateTime getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(LocalDateTime expirationDate) {
		this.expirationDate = expirationDate;
	}
}
