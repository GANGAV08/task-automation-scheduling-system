package com.assignment.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.assignment.dto.PayloadRequest;

import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 */
@Service
@Slf4j
public class MailService {

	private final JavaMailSender mailSender;
	private final Configuration freemarkerConfig;

	public MailService(JavaMailSender mailSender, Configuration freemarkerConfig) {
		this.mailSender = mailSender;
		this.freemarkerConfig = freemarkerConfig;
	}

	public void sendWelcomeEmail(PayloadRequest body) throws Exception {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(body.getEmail());
		helper.setSubject("Welcome to Our Platform");

		// Prepare template data
		Map<String, Object> model = new HashMap<>();
		model.put("email", body.getEmail());

		// Load FreeMarker template
		String text = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfig.getTemplate("welcome.ftl"),
				model);

		helper.setText(text, true);

		mailSender.send(message);
		log.info("Welcome email sent to {}", body.getEmail());
	}

	public void sendAdminNotification(String adminEmail, PayloadRequest body) throws Exception {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(adminEmail);
		helper.setSubject("New User Onboarded");

		// Prepare template data
		Map<String, Object> model = new HashMap<>();
		model.put("email", body.getEmail());

		// Load FreeMarker template
		String text = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfig.getTemplate("adminNotify.ftl"),
				model);

		helper.setText(text, true);

		mailSender.send(message);
		log.info("Welcome email sent to {}", body.getEmail());

	}
}
