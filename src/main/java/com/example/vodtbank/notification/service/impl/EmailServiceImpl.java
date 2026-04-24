package com.example.vodtbank.notification.service.impl;

import java.nio.charset.StandardCharsets;

import com.example.vodtbank.notification.dto.EmailDto;
import com.example.vodtbank.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

	private final JavaMailSender javaMailSender;
	private final TemplateEngine templateEngine;

	public EmailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;
	}

	@Override
	@Async
	public void sendEmail(EmailDto emailDto) {
		if(emailDto == null) {
			return;
		}

		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			helper.setTo(emailDto.getToEmail());
			helper.setSubject(emailDto.getSubject());

			if(StringUtils.hasText(emailDto.getTemplateName())) {
				Context context = new Context();
				context.setVariables(emailDto.getVariables());
				String htmlContent = templateEngine.process(emailDto.getTemplateName(), context);
				helper.setText(htmlContent, true);
			} else {
				helper.setText(emailDto.getBody(), true);
			}

			javaMailSender.send(message);
		} catch(MessagingException e) {
			log.error(e.getMessage());
		}
	}
}
