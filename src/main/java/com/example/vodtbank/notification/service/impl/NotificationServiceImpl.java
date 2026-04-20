package com.example.vodtbank.notification.service.impl;

import java.nio.charset.StandardCharsets;

import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.entity.Notification;
import com.example.vodtbank.notification.repository.NotificationRepository;
import com.example.vodtbank.notification.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class NotificationServiceImpl implements NotificationService {
	private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

	private final NotificationRepository notificationRepository;
	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;
	private final ModelMapper modelMapper;

	public NotificationServiceImpl(NotificationRepository notificationRepository, JavaMailSender javaMailSender,
			TemplateEngine templateEngine, ModelMapper modelMapper) {
		this.notificationRepository = notificationRepository;
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
		this.modelMapper = modelMapper;
	}

	@Override
	@Async
	@Transactional
	public void sendEmail(NotificationDto notificationDto, User user) {
		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			helper.setTo(notificationDto.getRecipient());
			helper.setSubject(notificationDto.getSubject());

			if(StringUtils.hasText(notificationDto.getTemplateName())) {
				Context context = new Context();
				context.setVariables(notificationDto.getVariables());
				String htmlContent = templateEngine.process(notificationDto.getTemplateName(), context);
				helper.setText(htmlContent, true);
			} else {
				helper.setText(notificationDto.getBody(), true);
			}

			javaMailSender.send(message);

			Notification notification = modelMapper.map(notificationDto, Notification.class);
			notification.setUser(user);

			notificationRepository.save(notification);
		} catch(MessagingException e) {
			log.error(e.getMessage());
		}

	}
}
