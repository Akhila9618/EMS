package com.example.authservice.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PasswordResetToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String token;
	private Instant expiryDate;
	private boolean used;
	// fetch property defines how to dependent table to be loaded
//	if lazy loads user table related data on demand only other option is eager -- which loads user related data in start only
	
	@CreationTimestamp
	private Instant cratedOn;
	@ManyToOne(fetch = FetchType.LAZY)
//	the name given to the joining column if we dont given the defualt name will be User entity name + _ +its entity primary column name
//	@JoinColumn(name = "userId")
	private User user;

}
