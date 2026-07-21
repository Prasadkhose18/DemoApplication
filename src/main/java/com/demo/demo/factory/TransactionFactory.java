package com.demo.demo.factory;


import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.service.TransactionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class TransactionFactory {

    public Transactions create(
            Accounts account,
            String type,
            BigDecimal amount,
            BigDecimal before,
            BigDecimal after
    ){
        Transactions txn = new Transactions();

        System.out.println("Transaction created");

        txn.setAccount(account);
        txn.setTransactionType("DEPOSIT");
        txn.setAmount(amount);
        txn.setBalanceBefore(before);
        txn.setBalanceAfter(after);
        txn.setTransactionTime(LocalDateTime.now());
        txn.setReferenceId(generateReferenceId());

        txn.setTransactionTime(LocalDateTime.now());
        return txn;
    }


    private String generateReferenceId() {
        return "TXN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();

    }

}


