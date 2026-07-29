package com.demo.demo.service;

import com.demo.demo.entity.Accounts;
import java.math.BigDecimal;

public interface ValidationService {

    void validateAmount(BigDecimal amount);

    void validateTransferAccounts(String fromAccountNumber, String toAccountNumber);

    void validateSufficientBalance(Accounts account, BigDecimal amount);

    Accounts validateOwnership(String accountNumber);

    Accounts getAccount(String accountNumber);

    Accounts getAccountForUpdate(String accountNumber);

    void updateBalance(Accounts account, BigDecimal newBalance);
}
