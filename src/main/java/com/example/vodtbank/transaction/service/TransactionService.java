package com.example.vodtbank.transaction.service;

import com.example.vodtbank.transaction.dto.TransactionRequest;
import com.example.vodtbank.transaction.dto.TransactionsResponse;

public interface TransactionService {
	void createTransaction(TransactionRequest transactionRequest);

	TransactionsResponse getTransactionsForAccount(String accountIdToken, int page, int size);
}
