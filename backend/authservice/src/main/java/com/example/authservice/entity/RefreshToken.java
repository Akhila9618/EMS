package com.example.authservice.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class RefreshToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String token;
	private Instant expiryDate;
	private boolean revoked;
	// if @ManyToOne got missed then during table creation this user column with
	// class type User id treated to map to one of the existing data types which
	// causes error
	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

}
