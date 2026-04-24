package com.example.vodtbank.notification.service;

import com.example.vodtbank.notification.dto.EmailDto;

public interface EmailService {
	void sendEmail(EmailDto emailDto);
}
