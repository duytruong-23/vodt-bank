package com.example.vodtbank.account;

import com.example.vodtbank.account.entity.Account;
import com.example.vodtbank.account.repository.AccountRepository;
import com.example.vodtbank.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AccountResolver {
	private final AccountRepository accountRepository;
	private final AccountEncrypter accountEncrypter;

	public AccountResolver(AccountRepository accountRepository, AccountEncrypter accountEncrypter) {
		this.accountRepository = accountRepository;
		this.accountEncrypter = accountEncrypter;
	}

	@Transactional
	public Account getAccountFromIdToken(String accountIdToken) {
		Long accountId = accountEncrypter.decrypt(accountIdToken);
		return accountRepository.findById(accountId)
				.orElseThrow(() -> new NotFoundException("Account not found"));
	}
}
