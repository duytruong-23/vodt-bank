package com.example.vodtbank.account.dto;

import com.example.vodtbank.common.dto.BaseDto;
import com.example.vodtbank.common.enums.AccountStatus;
import com.example.vodtbank.common.enums.AccountType;
import com.example.vodtbank.common.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDto extends BaseDto {
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;
    private Currency currency;
    private LocalDateTime closedAt;
}
