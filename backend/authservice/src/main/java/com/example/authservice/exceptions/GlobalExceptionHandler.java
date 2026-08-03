package com.example.authservice.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.example.authservice.dto.response.ApiResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

//	1.response structure problem  or else response fields validation mismatch
	@ExceptionHandler(MethodArgumentNotValidException.class)
	private ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
			MethodArgumentNotValidException ex) {
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		response.setMessage("Invalid Request");
		response.setStatus("failed");
		response.setData(errors);

		return ResponseEntity.badRequest().body(response);
	}

//2. @RequestParam / @PathVariable validation failures

	@ExceptionHandler(ConstraintViolationException.class)
	private ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationExcetpion(
			ConstraintViolationException ex) {
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage(ex.getMessage());
		return ResponseEntity.badRequest().body(response);

	}

//3. JSON corrupted
	@ExceptionHandler(HttpMessageNotReadableException.class)
	private ResponseEntity<ApiResponse<Map<String, String>>> handleHttpMessageNotReadableException(
			HttpMessageNotReadableException ex) {
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("Malformed JSON request.");
		return ResponseEntity.badRequest().body(response);
	}

//4.missing request parameter

	@ExceptionHandler(MissingServletRequestParameterException.class)
	private ResponseEntity<ApiResponse<Map<String, String>>> handleMissingServerRequestParameterException(
			MissingServletRequestParameterException ex) {
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage(ex.getParameterName() + "is missing");
		return ResponseEntity.badRequest().body(response);
	}

//5.request type mismatch
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	private ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentTypeMismatchException(
			MethodArgumentTypeMismatchException ex) {
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("Inappropriate data for " + ex.getName());
		return ResponseEntity.badRequest().body(response);
	}

//6.Database constraint violation error
	@ExceptionHandler(DataIntegrityViolationException.class)
	private ResponseEntity<ApiResponse<Map<String, String>>> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex) {
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("Database contraint violation");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

//7.Entity not found
	@ExceptionHandler(EntityNotFoundException.class)
	private ResponseEntity<ApiResponse<Map<String, String>>> handleEnityNotFoundException(EntityNotFoundException ex) {
		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

// 8. Illegal Arguments
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage(ex.getMessage());
		response.setData(null);

		return ResponseEntity.badRequest().body(response);
	}

	/*
	 * 9. Null Pointer Exceptions
	 */
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ApiResponse<String>> handleNullPointerException(NullPointerException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("Unexpected null value encountered.");
		response.setData(null);

		return ResponseEntity.internalServerError().body(response);
	}

	/*
	 * 10. Access Denied (Spring Security)
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("You are not authorized to access this resource.");
		response.setData(null);

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

	/*
	 * 11. Authentication Failure
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<String>> handleBadCredentialsException(BadCredentialsException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("Invalid username or password.");
		response.setData(null);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	/*
	 * 12. Unsupported HTTP Method
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<String>> handleHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage(ex.getMethod() + " method is not supported.");
		response.setData(null);

		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response);
	}

	/*
	 * 13. No Handler Found (404)
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleNoHandlerFoundException(NoHandlerFoundException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("Requested endpoint does not exist.");
		response.setData(null);

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	/*
	 * 14. File Size Exceeded
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiResponse<String>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("Uploaded file size exceeded.");
		response.setData(null);

		return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
	}

//	username not found exception
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage(ex.getMessage());
		response.setStatusCode(HttpStatus.NOT_FOUND.value());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiResponse<String>> handleIllegalStateException(IllegalStateException ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage(ex.getMessage());
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	/*
	 * 15. Catch-All Handler
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {

		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus("failed");
		response.setMessage("Something went wrong: " + ex.getMessage());
		response.setData(null);

		return ResponseEntity.internalServerError().body(response);
	}

}
