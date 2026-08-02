package com.example.authservice.dto.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
		@NotNull(message ="User name should not be null")
		 String userName,
		@NotNull(message = "User role should not be null")
		 String userRole,
		@Email
		@NotNull(message = "User email should not be null")
		 String userEmail		
		) {

}
