package com.demo.demo.service.impl;

import com.demo.demo.dto.response.BankStatementResponseDTO;
import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.Transactions;
import com.demo.demo.mapper.BankStatementMapper;
import com.demo.demo.repository.TransactionRepository;
import com.demo.demo.service.BankStatementService;
import com.demo.demo.service.BankStatementEmailService;
import com.demo.demo.service.ValidationService;
import com.demo.demo.security.services.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BankStatementServiceImpl implements BankStatementService {

    private final ValidationService validationService;
    private final TransactionRepository transactionRepository;
    private final BankStatementMapper bankStatementMapper;
    private final BankStatementEmailService bankStatementEmailService;
    private final CurrentUserService currentUserService;

    public BankStatementServiceImpl(ValidationService validationService,
                                    TransactionRepository transactionRepository,
                                    BankStatementMapper bankStatementMapper,
                                    BankStatementEmailService bankStatementEmailService,
                                    CurrentUserService currentUserService) {
        this.validationService = validationService;
        this.transactionRepository = transactionRepository;
        this.bankStatementMapper = bankStatementMapper;
        this.bankStatementEmailService = bankStatementEmailService;
        this.currentUserService = currentUserService;
    }

    @Override
    @Transactional(readOnly = true)
    public BankStatementResponseDTO generateStatement(String accountNumber,
                                                       LocalDate fromDate,
                                                       LocalDate toDate) {

        log.info("Generating statement. Account: {}, From: {}, To: {}",
                accountNumber, fromDate, toDate);

        validationService.validateStatementRequest(accountNumber, fromDate, toDate);

        // validateOwnership also allows admins and verifies account existence.
        Accounts account = validationService.validateOwnership(accountNumber);
        validationService.validateAccountIsActive(account);

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.plusDays(1).atStartOfDay();

        List<Transactions> transactions = transactionRepository
                .findByAccountAndTransactionTimeGreaterThanEqualAndTransactionTimeLessThanOrderByTransactionTimeAsc(
                        account, fromDateTime, toDateTime);

        BankStatementResponseDTO statement = bankStatementMapper.toResponseDTO(
                account, fromDate, toDate, transactions);

        bankStatementEmailService.sendStatement(
                statement,
                currentUserService.getCurrentUserEmail());

        log.info("Statement generated successfully. Account: {}, Transactions: {}",
                accountNumber, statement.getTotalTransactions());

        return statement;
    }
}
