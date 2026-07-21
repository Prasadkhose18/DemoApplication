package com.demo.demo.factory;


import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
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
            BigDecimal after) {

        Transactions txn = new Transactions();

        txn.setAccount(account);
        txn.setTransactionType(type);
        txn.setAmount(amount);
        txn.setBalanceBefore(before);
        txn.setBalanceAfter(after);
        txn.setTransactionTime(LocalDateTime.now());
        txn.setReferenceId(generateReferenceId());

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


