package com.example.authservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RefreshTokenRequest;
import com.example.authservice.dto.request.ResetPasswordRequest;
import com.example.authservice.dto.response.ApiResponse;
import com.example.authservice.service.AuthService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse> executeLogin(@RequestBody @Valid LoginRequest loginCredentials) {
		return ResponseEntity.ok(authService.executeLogin(loginCredentials));
	}

	@PostMapping("/generate-token")
	public ResponseEntity<ApiResponse> generateToken(@RequestBody @Valid LoginRequest loginCredentials) {
		return ResponseEntity.ok(authService.generateToken(loginCredentials));
	}
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse> refresh(@RequestBody RefreshTokenRequest request){
		return ResponseEntity.ok(authService.refresh(request));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse> logout(@RequestBody RefreshTokenRequest request){
		return ResponseEntity.ok(authService.logout(request));
	}
	
	@PostMapping("/forgot-password")
	public ResponseEntity<ApiResponse> forgotPassword(@RequestBody Map<String,String> request){
		String userName = request.get("userName");
		return ResponseEntity.ok(authService.forgotPassword(userName));
	}
	
	@PostMapping("/reset-password")
	public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request ){
		return ResponseEntity.ok(authService.resetPassword(request));
	}
}
