package com.example.vodtbank.notification.util;

import java.util.Map;

import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.dto.NotificationDto;

public class NotificationHelper {
	private NotificationHelper() {
		// Private constructor to prevent instantiation
	}

	public static NotificationDto createWelcomeNotification(String recipient) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject("Welcome to VodtBank!");
		notificationDto.setRecipient(recipient);
		return notificationDto;
	}

	public static EmailDto createWelcomeEmail(String recipient, String name) {
		EmailDto emailDto = new EmailDto();
		emailDto.setToEmail(recipient);
		emailDto.setSubject("Welcome to VodtBank!");
		emailDto.setTemplateName("welcome");
		emailDto.setVariables(Map.of("name", name));
		return emailDto;
	}
}
