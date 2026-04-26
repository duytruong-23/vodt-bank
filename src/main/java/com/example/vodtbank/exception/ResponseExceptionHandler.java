package com.example.vodtbank.exception;

import com.example.vodtbank.response.Response;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler {
	private final Log log = LogFactory.getLog(this.getClass());

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response<?>> handleUnknownException(Exception ex) {
		log.error(ex.getMessage(), ex);
		Response<?> response = Response.withError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"Internal server error");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Response<?>> handleNotFoundException(NotFoundException ex) {
		log.error(ex.getMessage(), ex);
		Response<?> response = Response.withError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<Response<?>> handleBadRequestException(BadRequestException ex) {
		log.error(ex.getMessage(), ex);
		Response<?> response = Response.withError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<Response<?>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
		log.error(ex.getMessage(), ex);
		Response<?> response = Response.withError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(InvalidTransactionException.class)
	public ResponseEntity<Response<?>> handleInvalidTransactionException(InvalidTransactionException ex) {
		log.error(ex.getMessage(), ex);
		Response<?> response = Response.withError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response<?>> handleValidationException(MethodArgumentNotValidException ex) {
		log.error(ex.getMessage(), ex);
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage())
				.reduce((msg1, msg2) -> msg1 + "; " + msg2)
				.orElse("Validation failed");

		Response<?> response = Response.withError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
}
