package com.demo.demo.repository;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {

    List<Transactions> findByAccount(Accounts account);

    List<Transactions> findByReferenceId(String referenceId);

    List<Transactions>
    findByAccountAndTransactionTimeGreaterThanEqualAndTransactionTimeLessThanOrderByTransactionTimeAsc(
            Accounts account,
            LocalDateTime fromDateTime,
            LocalDateTime toDateTime);

    List<Transactions> findAllByTransactionTimeBetweenOrderByTransactionTimeAsc(
            LocalDateTime start,
            LocalDateTime end
    );

    List<Transactions> findAllByTransactionTimeBetweenOrderByTransactionTimeDesc(LocalDateTime start, LocalDateTime end);

}
