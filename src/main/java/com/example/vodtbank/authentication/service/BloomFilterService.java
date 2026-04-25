package com.example.vodtbank.authentication.service;

public interface BloomFilterService {
	void addEmail(String email);

	boolean isEmailExisting(String email);
}
