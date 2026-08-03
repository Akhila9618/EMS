package com.example.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.authservice.dto.request.MailRequest;

@Service
public class EmailService {
	@Autowired
	JavaMailSender javaMailSender;
	
	public void sendEmail(MailRequest request) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(request.getTo());
		mailMessage.setSubject(request.getSubject());
		mailMessage.setText(request.getBody());
		javaMailSender.send(mailMessage);
		
	}
	
}
