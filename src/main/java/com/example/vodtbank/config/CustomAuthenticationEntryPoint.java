package com.example.vodtbank.config;

import java.io.IOException;

import com.example.vodtbank.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper mapper;

	public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		Response<?> responseBody = Response.withError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(mapper.writeValueAsString(responseBody));
	}
}
