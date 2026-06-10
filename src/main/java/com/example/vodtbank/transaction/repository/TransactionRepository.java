package com.example.vodtbank.transaction.repository;

import java.util.Optional;

import com.example.vodtbank.account.entity.Account;
import com.example.vodtbank.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

	Page<Transaction> findByFromAccountOrToAccount(Account account, Account account1, Pageable pageable);
}
