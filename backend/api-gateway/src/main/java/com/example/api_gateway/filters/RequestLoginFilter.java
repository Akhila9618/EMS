package com.example.api_gateway.filters;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
@Order(1)  // -- this defines execution order
public class RequestLoginFilter implements GlobalFilter {


	private static final Logger logger = LoggerFactory.getLogger(RequestLoginFilter.class);

//	this gets executed for every received request
	@Override   // this overrider is compulsory
	public Mono<Void> filter(ServerWebExchange exchange , GatewayFilterChain chain) {
		
			logger.info("----------------------");
			logger.info("Request received");
			logger.info("Path : {}", exchange.getRequest().getPath());
			logger.info("Method : {}", exchange.getRequest().getMethod());
			logger.info("Time : {}", LocalDateTime.now());
			logger.info("----------------------------------------");

			return chain.filter(exchange);
	}
}
