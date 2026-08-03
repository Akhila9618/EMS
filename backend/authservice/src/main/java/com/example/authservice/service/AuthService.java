package com.example.authservice.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.MailRequest;
import com.example.authservice.dto.request.RefreshTokenRequest;
import com.example.authservice.dto.request.ResetPasswordRequest;
import com.example.authservice.dto.response.ApiResponse;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.entity.PasswordResetToken;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.User;
import com.example.authservice.repository.PasswordResetRepository;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private RefreshTokenRepository refreshTokenRepo;

	@Autowired
	private PasswordResetRepository passowordResetRepo;

	@Autowired
	private JwtUtil jwtUtil;

	@Value("${uiHostUrl}")
	private String uiHostUrl;

	@Autowired
	private EmailService emailService;

	public ApiResponse executeLogin(LoginRequest loginCredentials) {
		log.info("[AuthService] Login request received for username: {}", loginCredentials.getUserName());
		ApiResponse response = new ApiResponse<>();

		User matchedUser = userRepository.findByUserName(loginCredentials.getUserName()).orElseThrow(() -> {
			log.warn("[auth service] [execute login] user not found with username : {}",
					loginCredentials.getUserName());
			return new UsernameNotFoundException("User " + loginCredentials.getUserName() + " not found");
		});

		if (loginCredentials.getPassword().isBlank() || loginCredentials.getPassword() == null) {
			log.warn("[AuthService] Login failed. Password is empty for user: {}", loginCredentials.getUserName());
			throw new IllegalArgumentException("Password cannot be empty.");
		} else {
//					if (!passwordEncoder.matches(loginCredentials.getPassword(), matchedUser.getUserPassoword())) {
			if (!loginCredentials.getPassword().equals(matchedUser.getUserPassowrd())) {
				log.warn("[AuthService] Login failed. Invalid password for user: {}", loginCredentials.getUserName());
				throw new BadCredentialsException("Invalid username or password.");

			}
			if (!(matchedUser.getActiveStatus().equalsIgnoreCase("active"))) {
				log.warn("[AuthService] Login denied. User account is inactive: {}", loginCredentials.getUserName());
				throw new AccessDeniedException("Your account has been deactivated. Please contact the administrator.");

			} else {
				log.info("User {} is succssfully logged in.", loginCredentials.getUserName());
				response.setStatus("success");
				response.setMessage("User logged successfully.");
				response.setStatusCode(HttpStatus.OK.value());
			}

		}

		return response;
	}

	public ApiResponse generateToken(LoginRequest loginCredentials) {
		log.info("[AuthService] Token generation initiated for user: {}", loginCredentials.getUserName());
		ApiResponse response = new ApiResponse<>();
		try {
			User matchedEmp = userRepository.findByUserName(loginCredentials.getUserName()).orElseThrow(() -> {
				log.info("User not found");
				throw new UsernameNotFoundException("user" + loginCredentials.getUserName() + "not found.");
			});
			String accessToken = jwtUtil.generateToken(matchedEmp);
			if (accessToken == null || accessToken.trim().isEmpty() && accessToken != null) {
				log.error("[AuthService] JWT generation returned null/empty for user: {}",
						loginCredentials.getUserName());

				throw new IllegalStateException("Unable to generate access token.");
			}
			RefreshToken refreshToken = refreshTokenService.generateRefreshToken(matchedEmp);
			if (refreshToken == null || refreshToken.getToken() == null || refreshToken.getToken().isBlank()) {
				log.error("[AuthService] Refresh token generation failed for user: {}", loginCredentials.getUserName());

				throw new IllegalStateException("Unable to generate refresh token.");
			}
			log.info("[AuthService] Refresh token generated successfully for user: {}", loginCredentials.getUserName());

			LoginResponse loginResponse = new LoginResponse();
			loginResponse.setRefreshToken(refreshToken.getToken());
			loginResponse.setAccessToken(accessToken);
			response.setMessage("successfully generated token");
			response.setStatus("success");
			response.setData(loginResponse);
			response.setStatusCode(HttpStatus.OK.value());

		} catch (Exception e) {
			e.printStackTrace();

		}
		return response;
	}

	public ApiResponse refresh(RefreshTokenRequest request) {
		log.info("[AuthService] Refresh token request received.");
		ApiResponse response = new ApiResponse();
		if (request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
			log.warn("[AuthService] Refresh token request rejected. Token is empty.");
			throw new IllegalArgumentException("Refresh token cannot be empty.");
		}
		RefreshToken refreshToken = refreshTokenRepo.findByToken(request.getRefreshToken()).orElseThrow(() -> {
			log.warn("[AuthService] Refresh failed. Invalid refresh token.");
			return new RuntimeException("Invalid refresh token");
		});

		if (refreshToken.isRevoked()) {
			log.warn("[AuthService] Refresh failed. Revoked refresh token used for user '{}'.",
					refreshToken.getUser().getUserName());
			throw new RuntimeException("Token already revoked.");
		}

		if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
			log.warn("[AuthService] Refresh token expired for user '{}'.", refreshToken.getUser().getUserName());
			throw new RuntimeException("RefreshToken expired");
		}

		String newAccessToken = jwtUtil.generateToken(refreshToken.getUser());

		LoginResponse loginResponse = new LoginResponse(newAccessToken, refreshToken.getToken());

		if (newAccessToken == null || newAccessToken.trim().isEmpty()) {
			log.error("[AuthService] Failed to generate access token for user '{}'.",
					refreshToken.getUser().getUserName());

			throw new IllegalStateException("Unable to generate access token.");
		}
		response.setStatus("success");
		response.setMessage("Token generated successfully");
		response.setData(loginResponse);
		response.setStatusCode(HttpStatus.OK.value());
		log.info("[AuthService] Refresh token flow completed successfully for user '{}'.",
				refreshToken.getUser().getUserName());
		return response;
	}

	public ApiResponse logout(RefreshTokenRequest refreshToken) {
		log.info("[AuthService] Logout request received.");
		if (refreshToken.getRefreshToken() == null || refreshToken.getRefreshToken().isBlank()) {
			log.warn("[AuthService] Logout failed. Refresh token is empty.");
			throw new IllegalArgumentException("Refresh token cannot be empty.");
		}

		ApiResponse response = new ApiResponse();
		// to set multiple token for a single user (device specific)
		RefreshToken token = refreshTokenRepo.findByToken(refreshToken.getRefreshToken()).orElseThrow(() -> {
			log.warn("[AuthService] Logout failed. Invalid refresh token.");
			return new RuntimeException("Invalid Refresh token.");
		});

		if (token.isRevoked()) {

			log.warn("[AuthService] Logout requested for an already revoked token.");

			throw new RuntimeException("Refresh token has already been revoked.");
		}
		token.setRevoked(true);
		refreshTokenRepo.save(token);
		response.setStatus("success");
		response.setMessage("User logged out successfully");
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

	public ApiResponse forgotPassword(String userName) {
		log.info("[AuthService] Password reset requested for user '{}'.", userName);

		ApiResponse response = new ApiResponse();
		User matchedUser = userRepository.findByUserName(userName).orElseThrow(() -> {
			log.warn("[AuthService] Password reset failed. User '{}' not found.", userName);
			return new UsernameNotFoundException("User not found");
		});
		PasswordResetToken token = new PasswordResetToken();
		String generatedToken = UUID.randomUUID().toString();
		token.setToken(generatedToken);
		token.setExpiryDate(Instant.now().plus(30, ChronoUnit.MINUTES));
		token.setUsed(false);
		token.setCratedOn(Instant.now());
		token.setUser(matchedUser);

		passowordResetRepo.save(token);
		log.info("[AuthService] Password reset token created for '{}'.", userName);
		String url = uiHostUrl + "reset-password?token=" + token.getToken();

		MailRequest request = new MailRequest();
		request.setSubject("Password reset mail");
		String emailBody = """
				Hello %s,

				We received a request to reset your password.

				Click the link below to change your password:
				%s

				This link expires in 30 minutes.

				If you didn't request this, please ignore this email.
				""".formatted(matchedUser.getUserName(), url);
		request.setBody(emailBody);
		request.setTo(matchedUser.getUserEmail());
		emailService.sendEmail(request);
		log.info("[AuthService] Password reset email sent to '{}'.", matchedUser.getUserEmail());

		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("token", generatedToken);
		response.setData(responseBody);
		response.setStatus("success");
		response.setMessage("Email is sent successfully.");
		response.setStatusCode(HttpStatus.OK.value());
		return response;
	}

	public ApiResponse resetPassword(ResetPasswordRequest request) {
		log.info("[AuthService] Password reset request received.");

		if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {

			log.warn("[AuthService] Password reset rejected. Empty password.");

			throw new IllegalArgumentException("New password cannot be empty.");
		}
		ApiResponse response = new ApiResponse();
		PasswordResetToken token = passowordResetRepo.findByToken(request.getToken()).orElseThrow(() -> {
			log.warn("[AuthService] Password reset failed. Invalid token.");
			return new RuntimeException("Invalid password reset token.");
		});

		User matchedUser = token.getUser();

		matchedUser.setUserPassowrd(request.getNewPassword());
		userRepository.save(matchedUser);
		token.setUsed(true);
		passowordResetRepo.save(token);
		response.setMessage("password reset is successfull.");
		response.setStatus("success");
		response.setStatusCode(HttpStatus.OK.value());

		return response;
	}

}
