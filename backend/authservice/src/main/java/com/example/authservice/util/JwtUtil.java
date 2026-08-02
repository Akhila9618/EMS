package com.example.authservice.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.authservice.dto.request.LoginRequest;
import com.example.authservice.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
	@Value("${tokenExpiryTime}")
	private Long tokenExpiryTime;

	@Value("${secretKey}")
	private String secretKey;

	public String generateToken( User matchedEmp) {
		String token = "";
		log.info(matchedEmp.getUserName() + matchedEmp.toString());
//		this type of declaration is immutable
//		Map<String, Object> totalClaims = Collections.emptyMap();
		Map<String, Object> totalClaims = new HashMap<>();
		totalClaims.put("role", matchedEmp.getUserRole());
		totalClaims.put("email", matchedEmp.getUserEmail());
		totalClaims.put("activeStatus", matchedEmp.getActiveStatus());
		log.info("claiims" + totalClaims.toString());

//		.setClaims() method explicitly expects a map with string keys: Map<String, Object>

		try {
			token = Jwts.builder().claims(totalClaims).subject(matchedEmp.getUserName()).issuedAt(new Date())
					.expiration(new Date(System.currentTimeMillis() + tokenExpiryTime))
					.signWith(Keys.hmacShaKeyFor(secretKey.getBytes())).compact();
			// compact() is used to convert to string before this step everything we built
			// is in JwtBuilder object form
			
		} catch (WeakKeyException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return token;
	}

}
