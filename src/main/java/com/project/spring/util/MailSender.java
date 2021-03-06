package com.project.spring.util;

import javax.inject.Inject;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailSender {
	
	@Inject
	JavaMailSender mailSender;
	
	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
	
	public void mailSend() {
		
		String id = "zimmyrabbit@naver.com";
		String toMail = "zimmyrabbit@naver.com";
		String title = "test";
		String content = "test";
		
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
			helper.setFrom(new InternetAddress(id, "dd"));
			helper.setTo(toMail);
			helper.setSubject(title);
			helper.setText(content,true);
			mailSender.send(message);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
