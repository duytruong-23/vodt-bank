package com.example.vodtbank.transaction.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.vodtbank.account.AccountResolver;
import com.example.vodtbank.account.entity.Account;
import com.example.vodtbank.account.repository.AccountRepository;
import com.example.vodtbank.authentication.entity.User;
import com.example.vodtbank.authentication.repository.UserRepository;
import com.example.vodtbank.authentication.service.UserService;
import com.example.vodtbank.common.enums.TransactionStatus;
import com.example.vodtbank.common.enums.TransactionType;
import com.example.vodtbank.exception.BadRequestException;
import com.example.vodtbank.exception.InsufficientBalanceException;
import com.example.vodtbank.exception.InvalidTransactionException;
import com.example.vodtbank.exception.NotFoundException;
import com.example.vodtbank.notification.dto.NotificationDto;
import com.example.vodtbank.notification.helper.NotificationHelper;
import com.example.vodtbank.notification.service.UserActionService;
import com.example.vodtbank.transaction.dto.TransactionDto;
import com.example.vodtbank.transaction.dto.TransactionRequest;
import com.example.vodtbank.transaction.dto.TransactionsResponse;
import com.example.vodtbank.transaction.entity.Transaction;
import com.example.vodtbank.transaction.repository.TransactionRepository;
import com.example.vodtbank.transaction.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(rollbackFor = Throwable.class)
public class TransactionServiceImpl implements TransactionService {
	private final TransactionRepository transactionRepository;
	private final AccountRepository accountRepository;
	private final ModelMapper modelMapper;
	private final AccountResolver accountResolver;
	private final UserActionService userActionService;
	private final UserService userService;
	private final UserRepository userRepository;

	public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository,
			ModelMapper modelMapper,
			AccountResolver accountResolver, UserActionService userActionService, UserService userService,
			UserRepository userRepository) {
		this.transactionRepository = transactionRepository;
		this.accountRepository = accountRepository;
		this.modelMapper = modelMapper;
		this.accountResolver = accountResolver;
		this.userActionService = userActionService;
		this.userService = userService;
		this.userRepository = userRepository;
	}

	@Override
	public void createTransaction(TransactionRequest transactionRequest) {
		Optional<Transaction> existingTransaction = transactionRepository.findByIdempotencyKey(
				transactionRequest.idempotencyKey());

		if(existingTransaction.isPresent()) {
			return;
		}

		Transaction transaction = new Transaction();

		transaction.setTransactionId(UUID.randomUUID());
		transaction.setTransactionType(transactionRequest.transactionType());
		transaction.setAmount(transactionRequest.amount());
		transaction.setDescription(transactionRequest.description());

		switch(transactionRequest.transactionType()) {
			case DEPOSIT -> handleDeposit(transactionRequest, transaction);
			case WITHDRAWAL -> handleWithdrawal(transactionRequest, transaction);
			case TRANSFER -> handleTransfer(transactionRequest, transaction);
			default -> throw new InvalidTransactionException("Invalid transaction type");
		}

		transaction.setTransactionStatus(TransactionStatus.SUCCESS);
		transaction.setTransactionDate(LocalDateTime.now());

		try {
			transactionRepository.save(transaction);
			sendTransactionNotification(transactionRequest);
		} catch(DataIntegrityViolationException e) {
			transactionRepository
					.findByIdempotencyKey(transactionRequest.idempotencyKey())
					.orElseThrow(() -> e);
		}
	}

	@Override
	public TransactionsResponse getTransactionsForAccount(String accountIdToken, int page, int size) {
		if(!StringUtils.hasText(accountIdToken)) {
			throw new BadRequestException("Account ID token is required");
		}

		Account account = accountResolver.getAccountFromIdToken(accountIdToken);
		String currentUserEmail = userService.getCurrentUserEmail();
		User user = userRepository.findByEmail(currentUserEmail)
				.orElseThrow(() -> new NotFoundException("User not found with email: " + currentUserEmail));

		if(!account.getUser().getId().equals(user.getId())) {
			throw new BadRequestException("Account does not belong to the current user");
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
		Page<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(account, account, pageable);

		List<TransactionDto> transactionDtos = transactions.getContent().stream()
				.map(transaction -> modelMapper.map(transaction, TransactionDto.class))
				.toList();
		return new TransactionsResponse(transactionDtos, transactions.getTotalPages(), transactions.getNumber() + 1,
				transactions.getSize(), transactions.getTotalElements());
	}

	private void handleDeposit(TransactionRequest transactionRequest, Transaction transaction) {
		Account account = accountResolver.getAccountFromIdToken(transactionRequest.toAccountIdToken());
		BigDecimal newBalance = account.getBalance().add(transactionRequest.amount());
		account.setBalance(newBalance);
		transaction.setToAccount(account);
		accountRepository.save(account);
	}

	private void handleWithdrawal(TransactionRequest transactionRequest, Transaction transaction) {
		Account account = accountResolver.getAccountFromIdToken(transactionRequest.fromAccountIdToken());

		if(account.getBalance().compareTo(transaction.getAmount()) < 0) {
			throw new InsufficientBalanceException("Insufficient funds for withdrawal");
		}

		BigDecimal newBalance = account.getBalance().subtract(transactionRequest.amount());
		account.setBalance(newBalance);
		transaction.setFromAccount(account);
		accountRepository.save(account);
	}

	private void handleTransfer(TransactionRequest transactionRequest, Transaction transaction) {
		if(transactionRequest.fromAccountIdToken().equals(transactionRequest.toAccountIdToken())) {
			throw new BadRequestException("Cannot transfer to the same account");
		}

		Account fromAccount = accountResolver.getAccountFromIdToken(transactionRequest.fromAccountIdToken());
		Account toAccount = accountResolver.getAccountFromIdToken(transactionRequest.toAccountIdToken());

		if(fromAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
			throw new InsufficientBalanceException("Insufficient funds for transfer");
		}

		BigDecimal newFromBalance = fromAccount.getBalance().subtract(transactionRequest.amount());
		BigDecimal newToBalance = toAccount.getBalance().add(transactionRequest.amount());

		fromAccount.setBalance(newFromBalance);
		toAccount.setBalance(newToBalance);

		transaction.setFromAccount(fromAccount);
		transaction.setToAccount(toAccount);

		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);
	}

	private void sendTransactionNotification(TransactionRequest transactionRequest) {
		final TransactionType transactionType = transactionRequest.transactionType();
		if(TransactionType.DEPOSIT.equals(transactionType)) {
			Account fromAccount = accountResolver.getAccountFromIdToken(transactionRequest.fromAccountIdToken());
			User user = fromAccount.getUser();
			NotificationDto notificationDto = NotificationHelper.createDepositTransactionNotification(user.getEmail(),
					user.getFirstName(), fromAccount.getAccountNumber(), transactionRequest.amount(),
					fromAccount.getCreatedAt(), fromAccount.getBalance());
			userActionService.sendAndCreateNotification(notificationDto, user.getId());
		} else if(TransactionType.WITHDRAWAL.equals(transactionType)) {
			Account fromAccount = accountResolver.getAccountFromIdToken(transactionRequest.fromAccountIdToken());
			User user = fromAccount.getUser();
			NotificationDto notificationDto = NotificationHelper.createWithdrawalTransactionNotification(
					user.getEmail(),
					user.getFirstName(), fromAccount.getAccountNumber(), transactionRequest.amount(),
					fromAccount.getCreatedAt(), fromAccount.getBalance());
			userActionService.sendAndCreateNotification(notificationDto, user.getId());
		} else if(TransactionType.TRANSFER.equals(transactionType)) {
			Account fromAccount = accountResolver.getAccountFromIdToken(transactionRequest.fromAccountIdToken());
			Account toAccount = accountResolver.getAccountFromIdToken(transactionRequest.toAccountIdToken());

			User fromUser = fromAccount.getUser();
			User toUser = toAccount.getUser();

			NotificationDto transferFromNotification = NotificationHelper.createWithdrawalTransactionNotification(
					fromUser.getEmail(), fromUser.getFirstName(), fromAccount.getAccountNumber(),
					transactionRequest.amount(), fromAccount.getCreatedAt(), fromAccount.getBalance());
			userActionService.sendAndCreateNotification(transferFromNotification, fromUser.getId());

			NotificationDto transferToNotification = NotificationHelper.createDepositTransactionNotification(
					toUser.getEmail(), toUser.getFirstName(), toAccount.getAccountNumber(), transactionRequest.amount(),
					toAccount.getCreatedAt(), toAccount.getBalance());
			userActionService.sendAndCreateNotification(transferToNotification, toUser.getId());
		}
	}
}
