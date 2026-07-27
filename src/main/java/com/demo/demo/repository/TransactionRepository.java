package com.demo.demo.repository;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transactions, Long> {

    List<Transactions> findByAccount(Accounts account);

    List<Transactions> findByReferenceId(String referenceId);
}