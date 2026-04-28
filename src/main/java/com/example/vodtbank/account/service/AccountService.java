package com.example.vodtbank.account.service;

import java.util.List;

import com.example.vodtbank.account.dto.AccountDto;
import com.example.vodtbank.common.enums.AccountType;

public interface AccountService {
	AccountDto createAccount(AccountType accountType, String userEmail);

	List<AccountDto> getAccounts();

	void closeAccount(String accountNumber);
}
