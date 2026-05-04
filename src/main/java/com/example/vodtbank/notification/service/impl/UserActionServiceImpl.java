package com.example.vodtbank.notification.service.impl;

import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.service.NotificationService;
import com.example.vodtbank.notification.service.UserActionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserActionServiceImpl implements UserActionService {
	private final Log log = LogFactory.getLog(this.getClass());
	private final NotificationService notificationService;
	private final ApplicationEventPublisher applicationEventPublisher;

	public UserActionServiceImpl(NotificationService notificationService,
			ApplicationEventPublisher applicationEventPublisher) {
		this.notificationService = notificationService;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	@Transactional
	public void sendAndCreateNotification(NotificationDto notificationDto, Long userId) {
		try {
			notificationService.createNotification(notificationDto, userId);

			applicationEventPublisher.publishEvent(notificationDto.getEmailContent());
		} catch(Exception e) {
			log.error(e);
		}
	}
}
