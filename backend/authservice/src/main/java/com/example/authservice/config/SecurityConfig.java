package com.example.authservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.authservice.filters.RequestLoggingFilter;

import com.example.authservice.filters.JwtFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;
	@Autowired
	private RequestLoggingFilter requestLoggingFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
//
	@Bean
	public SecurityFilterChain security(HttpSecurity http) throws Exception {
		return http
//				this is required when this service wants to manage cors
//				.cors(cors -> {})
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(
				        requestLoggingFilter,
				        UsernamePasswordAuthenticationFilter.class
				)
				.addFilterAfter(
				        jwtFilter,
				        RequestLoggingFilter.class
				)
				.build();
	}

//	this is required when this service wants to manage cors

//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		CorsConfiguration corsConf = new CorsConfiguration();
//		corsConf.addAllowedOrigin("http://localhost:5173");
//		corsConf.setAllowedMethods(List.of("GET", "PUT", "POST"));
//		corsConf.setAllowedHeaders(List.of("*"));
//		corsConf.setAllowCredentials(true);
//
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", corsConf);
//		return source;
//	}

}
