package com.example.vodtbank.transaction.service;

import java.util.List;

import com.example.vodtbank.transaction.dto.TransactionDto;
import com.example.vodtbank.transaction.dto.TransactionRequest;

public interface TransactionService {
	void createTransaction(TransactionRequest transactionRequest);

	List<TransactionDto> getTransactionsForAccount(String accountIdToken, int page, int size);
}
