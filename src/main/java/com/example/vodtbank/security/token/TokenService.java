package com.example.vodtbank.security.token;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {
	String generateToken(String email);

	String getUsernameFromToken(String token);

	boolean validateToken(String token, UserDetails userDetails);

	boolean isTokenExpired(String token);
}
