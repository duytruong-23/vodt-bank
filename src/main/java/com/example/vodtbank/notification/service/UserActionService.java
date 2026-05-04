package com.example.vodtbank.notification.service;

import com.example.vodtbank.notification.dto.NotificationDto;

public interface UserActionService {
	void sendAndCreateNotification(NotificationDto notificationDto, Long userId);
}
