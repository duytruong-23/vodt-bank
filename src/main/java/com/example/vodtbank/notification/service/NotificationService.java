package com.example.vodtbank.notification.service;

import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.notification.dto.NotificationDto;

public interface NotificationService {
	void sendEmail(NotificationDto notificationDto, User user);
}
