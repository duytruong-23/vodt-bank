package com.example.vodtbank.notification.service;

import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.dto.NotificationDto;

public interface UserActionService {
	void sendAndCreateNotification(EmailDto emailDto, NotificationDto notificationDto, Long userId);
}
