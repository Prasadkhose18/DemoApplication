package com.demo.demo.service;

import com.demo.demo.entity.Accounts;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface ValidationService {

    void validateAmount(BigDecimal amount);

    void validateTransferAccounts(String fromAccountNumber, String toAccountNumber);

    void validateSufficientBalance(Accounts account, BigDecimal amount);

    Accounts validateOwnership(String accountNumber);

    Accounts getAccount(String accountNumber);

    Accounts getAccountForUpdate(String accountNumber);

    void validateStatementRequest(String accountNumber,
                                  LocalDate fromDate,
                                  LocalDate toDate);

    void validateAccountIsActive(Accounts account);

    void updateBalance(Accounts account, BigDecimal newBalance);
}
