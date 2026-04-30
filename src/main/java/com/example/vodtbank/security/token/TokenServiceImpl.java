package com.example.vodtbank.security.token;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

	@Value("${jwt.public-path}")
	private String publicKeyPath;

	@Value("${jwt.private-path}")
	private String privateKeyPath;

	@Value("${jwt.expiration}")
	private long expirationTime;

	private PrivateKey privateKey;

	private PublicKey publicKey;

	@PostConstruct
	public void init() throws IOException {
		this.privateKey = getPrivateKey(privateKeyPath);
		this.publicKey = getPublicKey(publicKeyPath);
	}

	@Override
	public String generateToken(String email) {
		return Jwts.builder()
				.subject(email)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(privateKey)
				.compact();
	}

	@Override
	public String getUsernameFromToken(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	@Override
	public boolean validateToken(String token, UserDetails userDetails) {
		String username = getUsernameFromToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	@Override
	public boolean isTokenExpired(String token) {
		Date expiration = extractClaims(token, Claims::getExpiration);
		if(expiration != null) {
			return expiration.before(new Date());
		}
		return false;
	}

	private byte[] getDecodedKey(String filePath) throws IOException {
		Path path = Path.of(filePath);
		if(!Files.exists(path)) {
			throw new IOException("Key file not found: " + filePath);
		}

		String rawPem = Files.readString(path, StandardCharsets.UTF_8);

		String pem = rawPem.replaceAll("-----BEGIN (.*)-----", "")
				.replaceAll("-----END (.*)-----", "")
				.replaceAll("\\s", "");

		return Base64.getDecoder().decode(pem);
	}

	PrivateKey getPrivateKey(String filePath) throws IOException {
		byte[] decodedKey = getDecodedKey(filePath);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

		try {
			return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
		} catch(Exception e) {
			throw new IOException("Failed to load private key", e);
		}
	}

	PublicKey getPublicKey(String filePath) throws IOException {
		byte[] decodedKey = getDecodedKey(filePath);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);

		try {
			return KeyFactory.getInstance("RSA").generatePublic(keySpec);
		} catch(Exception e) {
			throw new IOException("Failed to load public key", e);
		}
	}

	private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parser()
				.verifyWith(publicKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claimsResolver.apply(claims);
	}
}
