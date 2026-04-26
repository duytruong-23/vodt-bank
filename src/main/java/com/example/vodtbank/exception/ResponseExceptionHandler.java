package com.example.vodtbank.exception;

import com.example.vodtbank.response.Response;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response<?>> handleUnknownException(Exception ex) {
		Response<?> response = Response.withError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Response<?>> handleNotFoundException(NotFoundException ex) {
		Response<?> response = Response.withError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException ex) {
		Response<?> response = Response.withError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<Response<?>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
		Response<?> response = Response.withError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(InvalidTransactionException.class)
	public ResponseEntity<Response<?>> handleInvalidTransactionException(InvalidTransactionException ex) {
		Response<?> response = Response.withError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}
