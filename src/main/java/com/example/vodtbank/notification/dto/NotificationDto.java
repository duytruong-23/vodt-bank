package com.example.vodtbank.notification.dto;

import com.example.vodtbank.common.dto.BaseDto;
import com.example.vodtbank.common.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;

public class NotificationDto extends BaseDto {
	private String subject;
	@NotBlank(message = "Recipient is required")
	private String recipient;
	private String body;
	private NotificationType type;
}
