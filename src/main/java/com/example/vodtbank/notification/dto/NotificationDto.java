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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}
}
