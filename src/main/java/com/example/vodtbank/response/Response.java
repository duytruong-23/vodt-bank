package com.example.vodtbank.response;

public class Response<T> {
	private final int statusCode;
	private final String message;
	private final T data;

	public Response(int statusCode, String message, T data) {
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
	}
}
