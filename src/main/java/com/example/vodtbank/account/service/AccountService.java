package com.example.vodtbank.account.service;

import java.util.List;

import com.example.vodtbank.account.dto.AccountDto;
import com.example.vodtbank.account.dto.AccountOverview;
import com.example.vodtbank.common.enums.AccountType;

public interface AccountService {
	AccountDto createAccount(AccountType accountType, String userEmail);

	List<AccountOverview> getCurrentUserAccounts();

	void closeAccount(String accountIdToken);
}
