package com.example.vodtbank.notification.helper;

import java.util.Map;

import com.example.vodtbank.common.enums.AccountType;
import com.example.vodtbank.common.enums.Currency;
import com.example.vodtbank.common.enums.NotificationType;
import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.dto.NotificationDto;

public class NotificationHelper {
	private static final String WELCOME_SUBJECT = "Welcome to VodtBank!";
	private static final String ACCOUNT_CREATED_SUBJECT = "Your New Account at VodtBank is Created!";
	private static final String PASSWORD_RESET_SUBJECT = "Password Reset Code";
	private static final String PASSWORD_UPDATE_SUBJECT = "Your Password Has Been Updated";

	private NotificationHelper() {
		// Private constructor to prevent instantiation
	}

	public static NotificationDto createWelcomeNotification(String recipient) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(WELCOME_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("Welcome to VodtBank! We're excited to have you on board.");
		notificationDto.setType(NotificationType.EMAIL);
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
		notificationDto.setBody("New Account Created!");
		notificationDto.setType(NotificationType.EMAIL);
		return notificationDto;
	}

	public static NotificationDto createPasswordResetNotification(String recipient) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(PASSWORD_RESET_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("Password Reset Code!");
		notificationDto.setType(NotificationType.EMAIL);
		return notificationDto;
	}

	public static EmailDto createPasswordResetEmail(String recipient, String name, String resetLink, String code) {
		Map<String, Object> variables = Map.of("name", name, "resetLink", resetLink + code);

		EmailDto emailDto = new EmailDto();
		emailDto.setToEmail(recipient);
		emailDto.setSubject(PASSWORD_RESET_SUBJECT);
		emailDto.setTemplateName("password-reset");
		emailDto.setVariables(variables);
		return emailDto;
	}

	public static EmailDto createPasswordUpdateEmail(String recipient, String name) {
		Map<String, Object> variables = Map.of("name", name);

		EmailDto emailDto = new EmailDto();
		emailDto.setToEmail(recipient);
		emailDto.setSubject(PASSWORD_UPDATE_SUBJECT);
		emailDto.setTemplateName("password-update");
		emailDto.setVariables(variables);
		return emailDto;
	}

	public  static NotificationDto createPasswordUpdateNotification(String recipient) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(PASSWORD_UPDATE_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("Your password has been updated successfully!");
		notificationDto.setType(NotificationType.EMAIL);
		return notificationDto;
	}
}
