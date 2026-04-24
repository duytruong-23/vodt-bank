package com.example.vodtbank.notification.util;

import java.util.Map;

import com.example.vodtbank.common.enums.AccountType;
import com.example.vodtbank.common.enums.Currency;
import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.dto.NotificationDto;

public class NotificationHelper {
	private static final String WELCOME_SUBJECT = "Welcome to VodtBank!";
	private static final String ACCOUNT_CREATED_SUBJECT = "Your New Account at VodtBank is Created!";

	private NotificationHelper() {
		// Private constructor to prevent instantiation
	}

	public static NotificationDto createWelcomeNotification(String recipient) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(WELCOME_SUBJECT);
		notificationDto.setRecipient(recipient);
		return notificationDto;
	}

	public static EmailDto createWelcomeEmail(String recipient, String name) {
		EmailDto emailDto = new EmailDto();
		emailDto.setToEmail(recipient);
		emailDto.setSubject(WELCOME_SUBJECT);
		emailDto.setTemplateName("welcome");
		emailDto.setVariables(Map.of("name", name));
		return emailDto;
	}

	public static EmailDto createNewAccountEmail(String recipient, String name, String accountNumber) {
		Map<String, Object> variables = Map.of("accountNumber", accountNumber, "name", name, "accountType",
				AccountType.SAVINGS.name(), "currency",
				Currency.VND.name());

		EmailDto emailDto = new EmailDto();
		emailDto.setToEmail(recipient);
		emailDto.setSubject(ACCOUNT_CREATED_SUBJECT);
		emailDto.setTemplateName("account-created");
		emailDto.setVariables(variables);
		return emailDto;
	}

	public static NotificationDto createNewAccountNotification(String recipient) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(ACCOUNT_CREATED_SUBJECT);
		notificationDto.setRecipient(recipient);
		return notificationDto;
	}
}
