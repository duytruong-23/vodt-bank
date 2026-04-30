package com.example.vodtbank.account.service.impl;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import com.example.vodtbank.account.AccountEncrypter;
import com.example.vodtbank.account.dto.AccountDto;
import com.example.vodtbank.account.dto.AccountOverview;
import com.example.vodtbank.account.entity.Account;
import com.example.vodtbank.account.repository.AccountRepository;
import com.example.vodtbank.account.service.AccountService;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.authentication.service.UserService;
import com.example.vodtbank.common.enums.AccountStatus;
import com.example.vodtbank.common.enums.AccountType;
import com.example.vodtbank.common.enums.Currency;
import com.example.vodtbank.exception.BadRequestException;
import com.example.vodtbank.exception.NotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Throwable.class)
public class AccountServiceImpl implements AccountService {
	private static final String ACCOUNT_NUMBER_PREFIX = "66";
	private static final int ACCOUNT_NUMBER_RANDOM_BOUND = 90_000_000;
	private static final int ACCOUNT_NUMBER_RANDOM_OFFSET = 10_000_000;

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final ModelMapper modelMapper;
	private final AccountEncrypter accountEncrypter;

	private final SecureRandom random = new SecureRandom();

	public AccountServiceImpl(AccountRepository accountRepository, UserRepository userRepository,
			UserService userService,
			ModelMapper modelMapper, AccountEncrypter accountEncrypter) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.modelMapper = modelMapper;
		this.accountEncrypter = accountEncrypter;
	}

	@Override
	public AccountDto createAccount(AccountType accountType, String userEmail) {
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new NotFoundException("User not found with email: " + userEmail));
		String accountNumber = generateAccountNumber();

		Account account = new Account();
		account.setAccountNumber(accountNumber);
		account.setAccountType(accountType);
		account.setUser(user);
		account.setCurrency(Currency.VND);
		account.setBalance(BigDecimal.ZERO);

		return modelMapper.map(accountRepository.save(account), AccountDto.class);
	}

	@Override
	public List<AccountOverview> getCurrentUserAccounts() {
		String currentUserEmail = userService.getCurrentUserEmail();
		User user = userRepository.findByEmail(currentUserEmail)
				.orElseThrow(() -> new NotFoundException("User not found with email: " + currentUserEmail));

		return user.getAccounts().stream().map(account -> {
			AccountOverview accountOverview = modelMapper.map(account, AccountOverview.class);
			accountOverview.setAccountIdToken(accountEncrypter.encrypt(account.getId()));
			return accountOverview;
		}).toList();
	}

	@Override
	public void closeAccount(String accountNumber) {
		String currentUserEmail = userService.getCurrentUserEmail();
		User user = userRepository.findByEmail(currentUserEmail)
				.orElseThrow(() -> new NotFoundException("User not found with email: " + currentUserEmail));
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new NotFoundException("Account not found with account number: " + accountNumber));

		if(!user.getAccounts().contains(account)) {
			throw new NotFoundException(
					"Account with account number " + accountNumber + " does not belong to user with email "
							+ currentUserEmail);
		}

		if(account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
			throw new BadRequestException("Cannot close account with non-zero balance");
		}

		account.setStatus(AccountStatus.CLOSED);
		account.setClosedAt(LocalDateTime.now());
		accountRepository.save(account);
	}

	private String generateAccountNumber() {
		String accountNumber;
		do {
			accountNumber = ACCOUNT_NUMBER_PREFIX + (random.nextInt(ACCOUNT_NUMBER_RANDOM_BOUND)
					+ ACCOUNT_NUMBER_RANDOM_OFFSET);
		} while(accountRepository.existsAccountByAccountNumber(accountNumber));

		return accountNumber;
	}
}
