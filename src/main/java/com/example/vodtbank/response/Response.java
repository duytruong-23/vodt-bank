package com.example.vodtbank.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response<T> (
	int statusCode,
	String message,
	T data
) {}
