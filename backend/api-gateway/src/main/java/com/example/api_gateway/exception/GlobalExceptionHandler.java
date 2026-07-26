package com.example.api_gateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.api_gateway.dto.response.ApiResponse;

import reactor.core.publisher.Mono;

@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable exception) {
		ApiResponse<Object> response = new ApiResponse<>();

		HttpStatus httpStatus;

		if (exception instanceof GatewayException) {
			GatewayException gatewayExceptions = (GatewayException) exception;
			switch (gatewayExceptions.getErrorCode()) {

			case "JWT_EXPIRED":

				httpStatus = HttpStatus.UNAUTHORIZED;
				break;

			case "TOKEN_INVALID":

				httpStatus = HttpStatus.UNAUTHORIZED;
				break;

			case "AUTH_HEADER_MISSING":

				httpStatus = HttpStatus.UNAUTHORIZED;
				break;

			default:

				httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			}

			response.setStatusCode(httpStatus.value());
			response.setStatus("FAILED");
			response.setMessage(gatewayExceptions.getMessage());
			response.setData(null);
		}
		else {
			 httpStatus =
	                    HttpStatus.INTERNAL_SERVER_ERROR;

	            response.setStatusCode(
	                    HttpStatus.INTERNAL_SERVER_ERROR.value());

	            response.setStatus("FAILED");
	            response.setMessage(
	                    "Something went wrong.");

	            response.setData(null);
		}
		return null;
	}
}
