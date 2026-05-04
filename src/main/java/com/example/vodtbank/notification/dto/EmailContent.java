package com.example.vodtbank.notification.dto;

import java.util.HashMap;
import java.util.Map;

public class EmailContent {
	private String toEmail;
	private String subject;
	private String body;
	private String templateName;
	private Map<String, Object> variables = new HashMap<>();

	public static EmailContent of(String toEmail, String subject, String templateName, Map<String, Object> variables) {
		EmailContent emailContent = new EmailContent();
		emailContent.setToEmail(toEmail);
		emailContent.setSubject(subject);
		emailContent.setTemplateName(templateName);
		emailContent.setVariables(variables);
		return emailContent;
	}

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
}
