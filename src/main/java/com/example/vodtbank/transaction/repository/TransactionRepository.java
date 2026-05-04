package com.example.vodtbank.transaction.repository;

import java.util.Optional;

import com.example.vodtbank.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
	Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
