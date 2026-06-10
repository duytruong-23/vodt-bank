package com.example.vodtbank.notification.helper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.example.vodtbank.common.enums.AccountType;
import com.example.vodtbank.common.enums.Currency;
import com.example.vodtbank.common.enums.NotificationType;
import com.example.vodtbank.notification.dto.EmailContent;
import com.example.vodtbank.notification.dto.NotificationDto;

public class NotificationHelper {
	private static final String WELCOME_SUBJECT = "Welcome to VodtBank!";
	private static final String ACCOUNT_CREATED_SUBJECT = "Your New Account at VodtBank is Created!";
	private static final String PASSWORD_RESET_SUBJECT = "Password Reset Code";
	private static final String PASSWORD_UPDATE_SUBJECT = "Your Password Has Been Updated";
	private static final String CREDIT_TRANSACTION_SUBJECT = "Credit Alert: You've Received a Deposit";
	private static final String DEBIT_TRANSACTION_SUBJECT = "Debit Alert: A Withdrawal Has Occurred";

	private NotificationHelper() {
		// Private constructor to prevent instantiation
	}

	public static NotificationDto createWelcomeNotification(String recipient, String name) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(WELCOME_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("Welcome to VodtBank! We're excited to have you on board.");
		notificationDto.setType(NotificationType.EMAIL);

		EmailContent emailContent = EmailContent.of(recipient, WELCOME_SUBJECT, "welcome", Map.of("name", name));

		notificationDto.setEmailContent(emailContent);
		return notificationDto;
	}

	public static NotificationDto createNewAccountNotification(String recipient, String name, String accountNumber,
			AccountType accountType, Currency currency) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(ACCOUNT_CREATED_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("New Account Created!");
		notificationDto.setType(NotificationType.EMAIL);

		Map<String, Object> variables = Map.of("accountNumber", accountNumber, "name", name, "accountType",
				accountType.name(), "currency", currency.name());

		EmailContent emailContent = EmailContent.of(recipient, ACCOUNT_CREATED_SUBJECT, "account-created", variables);

		notificationDto.setEmailContent(emailContent);
		return notificationDto;
	}

	public static NotificationDto createPasswordResetNotification(String recipient, String name, String resetLink,
			String code) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(PASSWORD_RESET_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("Password Reset Code!");
		notificationDto.setType(NotificationType.EMAIL);

		Map<String, Object> variables = Map.of("name", name, "resetLink", resetLink + code);

		EmailContent emailContent = EmailContent.of(recipient, PASSWORD_RESET_SUBJECT, "password-reset", variables);

		notificationDto.setEmailContent(emailContent);
		return notificationDto;
	}

	public static NotificationDto createPasswordUpdateNotification(String recipient, String name) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(PASSWORD_UPDATE_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("Your password has been updated successfully!");
		notificationDto.setType(NotificationType.EMAIL);

		Map<String, Object> variables = Map.of("name", name);

		EmailContent emailContent = EmailContent.of(recipient, PASSWORD_UPDATE_SUBJECT, "password-update", variables);

		notificationDto.setEmailContent(emailContent);
		return notificationDto;
	}

	public static NotificationDto createDepositTransactionNotification(String recipient, String name,
			String accountNumber,
			BigDecimal amount, LocalDateTime date, BigDecimal balance) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(CREDIT_TRANSACTION_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("Deposit Transaction!");
		notificationDto.setType(NotificationType.EMAIL);

		Map<String, Object> variables = createCommonTransactionEmailVariables(name, accountNumber, amount, date,
				balance);

		EmailContent emailContent = EmailContent.of(recipient, CREDIT_TRANSACTION_SUBJECT, "credit-alert", variables);

		notificationDto.setEmailContent(emailContent);
		return notificationDto;
	}

	public static NotificationDto createWithdrawalTransactionNotification(String recipient, String name,
			String accountNumber,
			BigDecimal amount, LocalDateTime date, BigDecimal balance) {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setSubject(DEBIT_TRANSACTION_SUBJECT);
		notificationDto.setRecipient(recipient);
		notificationDto.setBody("Withdrawal Transaction!");
		notificationDto.setType(NotificationType.EMAIL);

		Map<String, Object> variables = createCommonTransactionEmailVariables(name, accountNumber, amount, date,
				balance);

		EmailContent emailContent = EmailContent.of(recipient, DEBIT_TRANSACTION_SUBJECT, "debit-alert", variables);

		notificationDto.setEmailContent(emailContent);
		return notificationDto;
	}

	private static Map<String, Object> createCommonTransactionEmailVariables(String name, String accountNumber,
			BigDecimal amount,
			LocalDateTime date, BigDecimal balance) {
		return Map.of(
				"name", name,
				"accountNumber", accountNumber,
				"amount", amount,
				"date", date,
				"balance", balance
		);
	}
}
