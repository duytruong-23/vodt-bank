package com.example.vodtbank.notification.service;

import com.example.vodtbank.notification.dto.EmailContent;

public interface EmailService {
	void sendEmail(EmailContent emailContent);
}
