package com.example.vodtbank.transaction.dto;

import com.example.vodtbank.common.dto.BaseDto;
import com.example.vodtbank.common.enums.TransactionStatus;
import com.example.vodtbank.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto extends BaseDto {
    private Long id;

    private BigDecimal amount;

    private TransactionType transactionType;

    private TransactionStatus transactionStatus;

    private LocalDateTime transactionDate;

    private String description;

    // For transfer
    private String sourceAccount;
    private String destinationAccount;
}
