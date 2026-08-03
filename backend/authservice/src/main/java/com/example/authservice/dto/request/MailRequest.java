package com.example.authservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailRequest {
	private String subject;
	private String to;
	private String body;

}
