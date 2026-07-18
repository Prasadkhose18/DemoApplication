package com.demo.demo.repository;

import com.demo.demo.entity.Transactions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionReposiory extends JpaRepository<Transactions, Long> {

    Optional<Transactions> findByReferenceId(String referenceId);
    List<Transactions> findByAccountAccountId(Long accountId);

    List<Transactions> findByAccount_AccountNumberOrderByTransactionTimeDesc(
            String accountNumber
    );

    Page<Transactions> findByAccount_AccountNumberAndTransactionTimeBetween(
            String accountNumber,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );




}
