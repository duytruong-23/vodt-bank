package com.example.vodtbank.transaction.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.vodtbank.account.AccountResolver;
import com.example.vodtbank.account.entity.Account;
import com.example.vodtbank.account.repository.AccountRepository;
import com.example.vodtbank.common.enums.TransactionStatus;
import com.example.vodtbank.exception.BadRequestException;
import com.example.vodtbank.exception.InsufficientBalanceException;
import com.example.vodtbank.exception.InvalidTransactionException;
import com.example.vodtbank.transaction.dto.TransactionDto;
import com.example.vodtbank.transaction.dto.TransactionRequest;
import com.example.vodtbank.transaction.entity.Transaction;
import com.example.vodtbank.transaction.repository.TransactionRepository;
import com.example.vodtbank.transaction.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Throwable.class)
public class TransactionServiceImpl implements TransactionService {
	private final TransactionRepository transactionRepository;
	private final AccountRepository accountRepository;
	private final ModelMapper modelMapper;
	private final AccountResolver accountResolver;

	public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository,
			ModelMapper modelMapper,
			AccountResolver accountResolver) {
		this.transactionRepository = transactionRepository;
		this.accountRepository = accountRepository;
		this.modelMapper = modelMapper;
		this.accountResolver = accountResolver;
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
		} catch(DataIntegrityViolationException e) {
			transactionRepository
					.findByIdempotencyKey(transactionRequest.idempotencyKey())
					.orElseThrow(() -> e);
		}
	}

	@Override
	public List<TransactionDto> getTransactionsForAccount(String accountIdToken, int page, int size) {
		return List.of();
	}

	private void handleDeposit(TransactionRequest transactionRequest, Transaction transaction) {
		Account account = accountResolver.getAccountFromIdToken(transactionRequest.fromAccountIdToken());
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
}
