package com.example.vodtbank.account;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AccountEncrypter {

	private static final String ALGO = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";

	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH = 128;

	private final SecureRandom secureRandom = new SecureRandom();
	private final Log log = LogFactory.getLog(getClass());

	@Value("${account.encryption.secret-key-path}")
	private String secretKeyPath;

	private byte[] secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = loadSecretKey();
	}

	public String encrypt(Long accountId) {
		try {
			byte[] iv = new byte[IV_LENGTH];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			SecretKeySpec keySpec = new SecretKeySpec(this.secretKey, ALGO);
			GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);

			cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

			byte[] plaintext = accountId.toString().getBytes(StandardCharsets.UTF_8);
			byte[] ciphertext = cipher.doFinal(plaintext);

			// Combine IV + ciphertext
			ByteBuffer buffer = ByteBuffer.allocate(iv.length + ciphertext.length);
			buffer.put(iv);
			buffer.put(ciphertext);

			// URL-safe Base64
			return Base64.getUrlEncoder()
					.withoutPadding()
					.encodeToString(buffer.array());
		} catch(Exception e) {
			log.error(e);
			throw new RuntimeException("Account encryption failed", e);
		}
	}

	public Long decrypt(String token) {
		try {
			byte[] decoded = Base64.getUrlDecoder().decode(token);

			ByteBuffer buffer = ByteBuffer.wrap(decoded);

			byte[] iv = new byte[IV_LENGTH];
			buffer.get(iv);

			byte[] ciphertext = new byte[buffer.remaining()];
			buffer.get(ciphertext);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			SecretKeySpec keySpec = new SecretKeySpec(this.secretKey, ALGO);
			GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);

			cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

			byte[] plaintext = cipher.doFinal(ciphertext);

			return Long.parseLong(new String(plaintext, StandardCharsets.UTF_8));
		} catch(Exception e) {
			log.error(e);
			throw new RuntimeException("Account decryption failed", e);
		}
	}

	private byte[] loadSecretKey() {
		Path filePath = Path.of(secretKeyPath);

		if(!Files.exists(filePath)) {
			throw new IllegalStateException("Secret key file not found: " + secretKeyPath);
		}

		byte[] key;
		try {
			key = Files.readAllBytes(filePath);
		} catch(IOException e) {
			throw new RuntimeException("Failed to read secret key file", e);
		}

		if(key.length == 0) {
			throw new IllegalStateException("Secret key file is empty");
		}

		return key;
	}
}
