package com.example.vodtbank.security.token;

import java.io.IOException;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {
	String generateToken(String email) throws IOException;

	String getUsernameFromToken(String token) throws IOException;

	boolean validateToken(String token, UserDetails userDetails) throws IOException;

	boolean isTokenExpired(String token) throws IOException;
}
