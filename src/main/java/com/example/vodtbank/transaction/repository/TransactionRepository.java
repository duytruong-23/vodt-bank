package com.example.vodtbank.transaction.repository;

import java.util.List;
import java.util.Optional;

import com.example.vodtbank.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccount_AccountNumber(String accountNumber, Pageable pageable);

    List<Transaction> findByAccount_AccountNumber(String accountNumber);

	Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}
