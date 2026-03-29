package com.example.vodtbank.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResetPasswordRequest(String email, String code, String newPassword) {}
