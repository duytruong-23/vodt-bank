package com.example.vodtbank.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
	private final int statusCode;

	private final String message;

	private final T data;

	public Response(int statusCode, String message, T data) {
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
	}

	public static <T> Response<T> withStatus(int statusCode, String message, T data) {
		return new Response<>(statusCode, message, data);
	}

	public static <T> Response<T> success(String message, T data) {
		return new Response<>(200, message, data);
	}

	public  static <T> Response<T> noContent(String message) {
		return new Response<>(204, message, null);
	}

	public static <T> Response<T> withError(int statusCode, String message) {
		return new Response<>(statusCode, message, null);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}
}
