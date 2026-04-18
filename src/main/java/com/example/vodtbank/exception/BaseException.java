package com.example.vodtbank.exception;

public abstract class BaseException extends RuntimeException {
	protected BaseException(String message) {
		super(message);
	}
}
