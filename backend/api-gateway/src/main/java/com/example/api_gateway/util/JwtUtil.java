package com.example.api_gateway.util;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.api_gateway.exception.GatewayException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	@Value("${secretKey}")
	private String secretKey;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public void validateToken(String token) {

	    try {

	    	extractAllclaims(token);

	    } catch (ExpiredJwtException e) {

	        throw new GatewayException(
	                "JWT_EXPIRED",
	                "JWT Token has expired");

	    } catch (JwtException e) {

	        throw new GatewayException(
	                "TOKEN_INVALID",
	                "Invalid JWT Token");
	    }

	}
	public Claims extractAllclaims(String token) {

		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}


	public String extractRole(String token) {
		
		return extractAllclaims(token).get("role",String.class);
	}

	public String extractUserName(String token) {
	
		return extractAllclaims(token).getSubject();
	}

}
