package com.example.vodtbank.account.repository;

import java.util.List;
import java.util.Optional;

import com.example.vodtbank.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserId(Long UserId);

	boolean existsAccountByAccountNumber(String accountNumber);
}
