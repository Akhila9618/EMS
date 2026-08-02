package com.example.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter

//@Data
public class LoginRequest {
	@NotBlank(message = "username should not be empty")
	private String userName;
	@NotBlank(message = "password should not be empty")
	private String password;

}
