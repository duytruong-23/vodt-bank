package com.example.vodtbank.notification.service;

import com.example.vodtbank.notification.dto.NotificationDto;

public interface NotificationService {
	NotificationDto createNotification(NotificationDto notificationDto, Long userId);
}
