package com.example.vodtbank.exception;

import com.example.vodtbank.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response<?>> handleUnknownException(Exception ex) {
		Response<?> response = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Response<?>> handleNotFoundException(NotFoundException ex) {
		Response<?> response = new Response<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException ex) {
		Response<?> response = new Response<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<Response<?>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
		Response<?> response = new Response<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(InvalidTransactionException.class)
	public ResponseEntity<Response<?>> handleInvalidTransactionException(InvalidTransactionException ex) {
		Response<?> response = new Response<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}
