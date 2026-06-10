package com.example.vodtbank.transaction.dto;

import java.math.BigDecimal;

import com.example.vodtbank.common.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
		@NotNull(message = "Transaction type is required")
        TransactionType transactionType,
        BigDecimal amount,
        String fromAccountIdToken,
        String description,
        String toAccountIdToken,
		@NotBlank(message = "Idempotency key is required")
		String idempotencyKey
) {
}
