package com.example.vodtbank.config;

import java.io.IOException;

import com.example.vodtbank.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	private final ObjectMapper mapper;

	public CustomAccessDeniedHandler(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		Response<?> responseBody = Response.withError(HttpServletResponse.SC_FORBIDDEN,
				accessDeniedException.getMessage());

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().write(mapper.writeValueAsString(responseBody));
	}
}
