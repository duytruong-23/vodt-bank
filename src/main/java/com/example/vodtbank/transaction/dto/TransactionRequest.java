package com.example.vodtbank.transaction.dto;

import com.example.vodtbank.common.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionRequest(
        TransactionType transactionType,
        BigDecimal amount,
        String accountNumber,
        String description,
        // The receiver account number for transfer transactions
        String destinationAccountNumber
) {
}
