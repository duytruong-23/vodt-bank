package com.example.vodtbank.account.dto;

import java.math.BigDecimal;

import com.example.vodtbank.common.enums.AccountStatus;
import com.example.vodtbank.common.enums.AccountType;
import com.example.vodtbank.common.enums.Currency;

public class AccountOverview {
	private String accountIdToken;
	private AccountType accountType;
	private BigDecimal balance;
	private Currency currency;
	private AccountStatus status;

	public String getAccountIdToken() {
		return accountIdToken;
	}

	public void setAccountIdToken(String accountIdToken) {
		this.accountIdToken = accountIdToken;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}
}
