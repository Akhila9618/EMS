package com.example.authservice.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken , Long> {
	
//	@Query("""
//		       SELECT rt
//		       FROM RefreshToken rt
//		       WHERE rt.user.userName = :userName
//		       """)
//	
//	Optional<RefreshToken> findByUserName(@Param("userName") String userName);
//	jpa internally performs fetching logic
//	Optional<RefreshToken> findByUser_UserName(String userName);

	Optional<RefreshToken> findByToken(String refreshToken);
	
	
	
	
	

}
