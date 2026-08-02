package com.example.authservice.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.User;
import com.example.authservice.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
	@Autowired
	private RefreshTokenRepository refreshTokenRepo;
	
	
//	refresh token is used because uuid strored in db is easily recoverable
	public RefreshToken generateRefreshToken(User user) {
		RefreshToken token = new RefreshToken();
		token.setToken(UUID.randomUUID().toString());
		token.setExpiryDate(Instant.now().plus(7,ChronoUnit.DAYS));
		token.setUser(user);
		return refreshTokenRepo.save(token);			
	}

}
