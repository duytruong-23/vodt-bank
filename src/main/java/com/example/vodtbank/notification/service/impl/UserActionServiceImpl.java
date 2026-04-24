package com.example.vodtbank.notification.service.impl;

import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.service.EmailService;
import com.example.vodtbank.notification.service.NotificationService;
import com.example.vodtbank.notification.service.UserActionService;
import org.springframework.stereotype.Service;

@Service
public class UserActionServiceImpl implements UserActionService {
	private final EmailService emailService;
	private final NotificationService notificationService;

	public UserActionServiceImpl(EmailService emailService, NotificationService notificationService) {
		this.emailService = emailService;
		this.notificationService = notificationService;
	}

	@Override
	public void sendAndCreateNotification(EmailDto emailDto, NotificationDto notificationDto, Long userId) {
		notificationService.createNotification(notificationDto, userId);
		emailService.sendEmail(emailDto);
	}
}
