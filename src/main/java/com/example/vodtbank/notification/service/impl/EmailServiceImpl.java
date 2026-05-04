package com.example.vodtbank.notification.service.impl;

import java.nio.charset.StandardCharsets;

import com.example.vodtbank.notification.dto.EmailContent;
import com.example.vodtbank.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
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
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void sendEmail(EmailContent emailContent) {
		if(emailContent == null) {
			return;
		}

		MimeMessage message = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			helper.setTo(emailContent.getToEmail());
			helper.setSubject(emailContent.getSubject());

			if(StringUtils.hasText(emailContent.getTemplateName())) {
				Context context = new Context();
				context.setVariables(emailContent.getVariables());
				String htmlContent = templateEngine.process(emailContent.getTemplateName(), context);
				helper.setText(htmlContent, true);
			} else {
				helper.setText(emailContent.getBody(), true);
			}

			javaMailSender.send(message);
		} catch(MessagingException e) {
			log.error(e.getMessage());
		}
	}
}
