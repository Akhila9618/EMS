package com.example.authservice.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.dto.request.RefreshTokenRequest;
import com.example.authservice.dto.response.ApiResponse;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.User;
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
	private JwtUtil jwtUtil;

	public ApiResponse executeLogin(LoginRequest loginCredentials) {
		ApiResponse response = new ApiResponse<>();
		log.info("::::" + loginCredentials.toString() + loginCredentials.getUserName());
		try {
			User matchedUser = userRepository.findByUserName(loginCredentials.getUserName()).orElseThrow(() -> {
				log.warn("[auth service] [execute login] user not found with username : {}",
						loginCredentials.getUserName());

				return new UsernameNotFoundException("User " + loginCredentials.getUserName() + " not found");
			});

			if (matchedUser == null) {
				log.info("User not found");
				response.setMessage("failed");
				response.setMessage("User not found.");
				response.setStatusCode(HttpStatus.NOT_FOUND.value());
			} else {
				if (loginCredentials.getPassword().isBlank() || loginCredentials.getPassword() == null) {
					log.info("Password is empty");
					response.setMessage("failed");
					response.setMessage("Password can't be null");
					response.setStatusCode(HttpStatus.NOT_FOUND.value());
				} else {
//					if (!passwordEncoder.matches(loginCredentials.getPassword(), matchedUser.getUserPassoword())) {
					log.info(loginCredentials.getPassword() + " pass " + matchedUser.getUserPassowrd());
					if (!loginCredentials.getPassword().equals(matchedUser.getUserPassowrd())) {
						log.info("Password is invalid passoword");
						response.setMessage("failed");
						response.setMessage("Password is invalid");
						response.setStatusCode(HttpStatus.BAD_REQUEST.value());

					} else {
						if (!(matchedUser.getActiveStatus().equalsIgnoreCase("active"))) {
							log.info("User is deactivated.");
							response.setStatus(null);
							response.setMessage("User is deactivated.");
							response.setStatusCode(HttpStatus.FORBIDDEN.value());
						} else {
							log.info("User {} is succssfully logged in.", loginCredentials.getUserName());
							response.setStatus("success");
							response.setMessage("User logged successfully.");
							response.setStatusCode(HttpStatus.OK.value());
						}

					}

				}
			}
		} catch (Exception e) {
			log.info("Something went wrong.");
			response.setMessage(e.getMessage());
			response.setMessage("Something went wrong.");

		}

		return response;
	}

	public ApiResponse generateToken(LoginRequest loginCredentials) {
		ApiResponse response = new ApiResponse<>();
		try {
			User matchedEmp = userRepository.findByUserName(loginCredentials.getUserName()).orElseThrow(() -> {
				log.info("User not found");
				return new RuntimeException("user" + loginCredentials.getUserName() + "not found.");
			});
			String accessToken = jwtUtil.generateToken(matchedEmp);
			RefreshToken refreshToken = refreshTokenService.generateRefreshToken(matchedEmp);
			if (accessToken == null || accessToken.trim().isEmpty() && accessToken != null) {
				response.setStatus("failed");
				response.setMessage("Error while generating token");
			} else {
				LoginResponse loginResponse = new LoginResponse();
				loginResponse.setRefreshToken(refreshToken.getToken());
				loginResponse.setAccessToken(accessToken);
				response.setMessage("successfully generated token");
				response.setStatus("success");
				response.setData(loginResponse);
				response.setStatusCode(HttpStatus.OK.value());
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return response;
	}

	public ApiResponse refresh(RefreshTokenRequest request) {
		ApiResponse response = new ApiResponse();
		
		RefreshToken refreshToken = refreshTokenRepo.findByToken(request.getRefreshToken())
				.orElseThrow(() -> new RuntimeException("Invalid refresh token"));
		
		if(refreshToken.isRevoked()) {
			throw new RuntimeException("Token already revoked.");
		}

		if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
			throw new RuntimeException("RefreshToken expired");
		}

		String newAccessToken = jwtUtil.generateToken(refreshToken.getUser());

		LoginResponse loginResponse = new LoginResponse(newAccessToken, refreshToken.getToken());

		if (newAccessToken == null || newAccessToken.trim().isEmpty()) {
			response.setStatus("failed");
			response.setMessage("Error while generating token.");
			response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		} else {
			response.setStatus("success");
			response.setMessage("Token generated successfully");
			response.setData(loginResponse);
			response.setStatusCode(HttpStatus.OK.value());
		}

		return response;
	}
	
	public ApiResponse logout(RefreshTokenRequest refreshToken) {
		ApiResponse response = new ApiResponse();
		
		//to set multiple token for a single user (device specific) 
		RefreshToken token = refreshTokenRepo.findByToken(refreshToken.getRefreshToken()).orElseThrow(() -> new RuntimeException("An ErrorOccured"));
		
		
		token.setRevoked(true);
		try {
			refreshTokenRepo.save(token);
			response.setStatus("success");
			response.setMessage("User logged out successfully");
			response.setStatusCode(HttpStatus.OK.value());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setStatus("failed");
			response.setMessage("Failed to logout user.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
		return response;
	}

}
