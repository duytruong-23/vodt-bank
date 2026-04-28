package com.example.vodtbank.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.vodtbank.common.dto.BaseDto;
import com.example.vodtbank.common.enums.AccountStatus;
import com.example.vodtbank.common.enums.AccountType;
import com.example.vodtbank.common.enums.Currency;

public class AccountDto extends BaseDto {
    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;
    private Currency currency;
    private LocalDateTime closedAt;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public LocalDateTime getClosedAt() {
		return closedAt;
	}

	public void setClosedAt(LocalDateTime closedAt) {
		this.closedAt = closedAt;
	}
}
