package com.example.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@EnableWebFlux
public class SecurityConfig {
//	@Bean
//	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//		return http.csrf(csrf -> csrf.disable()).authorizeExchange(exchange -> exchange.anyExchange().permitAll())
//				.build();
//	}

	@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

		http.csrf(ServerHttpSecurity.CsrfSpec::disable);

		http.authorizeExchange(exchanges -> exchanges.anyExchange().permitAll());

		return http.build();
	}

};
