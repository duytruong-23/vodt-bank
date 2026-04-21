package com.example.vodtbank.security;

import java.io.IOException;

import com.example.vodtbank.config.CustomAuthenticationEntryPoint;
import com.example.vodtbank.security.token.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AuthFilter extends OncePerRequestFilter {
	private static final String TOKEN_PREFIX = "Bearer ";

	private final TokenService tokenService;
	private final CustomAuthenticationEntryPoint authenticationEntryPoint;
	private final CustomUserDetailsService customUserDetailsService;

	public AuthFilter(TokenService tokenService, CustomAuthenticationEntryPoint authenticationEntryPoint,
			CustomUserDetailsService customUserDetailsService) {
		this.tokenService = tokenService;
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException {
		final String token = extractToken(request);

		if(token != null) {
			String email;
			try {
				email = tokenService.getUsernameFromToken(token);
			} catch(Exception e) {
				logger.error("Token processing error");
				AuthenticationException authenticationException = new BadCredentialsException(e.getMessage());
				authenticationEntryPoint.commence(request, response, authenticationException);
				return;
			}

			UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

			if(StringUtils.hasText(email) && tokenService.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// Set the authentication in the SecurityContext
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		try {
			filterChain.doFilter(request, response);
		} catch(Exception e) {
			logger.error(e.getMessage());
			AuthenticationException authenticationException = new BadCredentialsException(e.getMessage());
			authenticationEntryPoint.commence(request, response, authenticationException);
		}
	}

	private String extractToken(HttpServletRequest request) {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(header != null && header.startsWith(TOKEN_PREFIX)) {
			return header.substring(TOKEN_PREFIX.length());
		}
		return null;
	}
}
