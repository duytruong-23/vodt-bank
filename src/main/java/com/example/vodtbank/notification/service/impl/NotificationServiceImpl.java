package com.example.vodtbank.notification.service.impl;

import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.exception.NotFoundException;
import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.entity.Notification;
import com.example.vodtbank.notification.repository.NotificationRepository;
import com.example.vodtbank.notification.service.NotificationService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final ModelMapper modelMapper;
	private final UserRepository userRepository;

	public NotificationServiceImpl(NotificationRepository notificationRepository, ModelMapper modelMapper,
			UserRepository userRepository) {
		this.notificationRepository = notificationRepository;
		this.modelMapper = modelMapper;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public NotificationDto createNotification(NotificationDto notificationDto, Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
		Notification notification = modelMapper.map(notificationDto, Notification.class);
		notification.setUser(user);

		return modelMapper.map(notificationRepository.save(notification), NotificationDto.class);
	}
}
