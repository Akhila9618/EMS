package com.example.api_gateway.util;

import java.util.Map;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.api_gateway.constatns.HeaderConstants;

@Component
public class HeaderUtil {

	public ServerWebExchange addHeaders(ServerWebExchange exchange, String userName, String role,
			Map<String, Object> claims) {

		ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().header(HeaderConstants.USERNAME, userName)
				.header(HeaderConstants.ROLE, role).build();
		
		return exchange.mutate().request(modifiedRequest).build();
	}

}
