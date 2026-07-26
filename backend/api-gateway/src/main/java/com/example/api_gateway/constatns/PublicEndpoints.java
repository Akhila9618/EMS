package com.example.api_gateway.constatns;

import java.util.List;

public class PublicEndpoints {

	public static final List<String> publicUrls = List.of("/auth.login/", "/auth/forgot-password",
			"/auth/reset-password", "/swagger-ui", "/v3/api-docs");
}
