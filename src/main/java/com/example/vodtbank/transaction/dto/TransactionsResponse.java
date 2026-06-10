package com.example.vodtbank.transaction.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionsResponse {
	private List<TransactionDto> transactions;
	private int totalPages;
	private int currentPage;
	private int pageSize;
	private long totalElements;
}
