package com.demo.demo.service.impl;

import com.demo.demo.dto.response.DailyAuditReportDTO;
import com.demo.demo.dto.response.TransactionAuditDTO;
import com.demo.demo.entity.Transactions;
import com.demo.demo.enums.TransactionType;
import com.demo.demo.repository.TransactionRepository;
import com.demo.demo.service.AuditReportService;
import com.demo.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditReportServiceImpl implements AuditReportService {

    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    @Override
    @Deprecated
    public void sendAuditReportEmail() {
        log.warn("sendAuditReportEmail is deprecated. Use AuditScheduler.sendHourlyAuditReport() instead.");
    }

    @Override
    public DailyAuditReportDTO generateDailyAuditReport() {
        LocalDate today = LocalDate.now();
        return generateAuditReport(today, today);
    }

    @Override
    @Transactional(readOnly = true)
    public DailyAuditReportDTO generateAuditReport(
            LocalDate fromDate,
            LocalDate toDate
    ) {

        log.info(
                "Generating audit report from {} to {}.",
                fromDate,
                toDate
        );

        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(LocalTime.MAX);

        List<Transactions> transactions =
                transactionRepository
                        .findAllByTransactionTimeBetweenOrderByTransactionTimeDesc(
                                start,
                                end
                        );

        log.info(
                "Found {} transactions.",
                transactions.size()
        );

        List<TransactionAuditDTO> auditTransactions =
                transactions.stream()
                        .map(this::convertToAuditDTO)
                        .toList();

        Predicate<Transactions> deposit =
                transaction ->
                        transaction.getTransactionType() == TransactionType.DEPOSIT;

        Predicate<Transactions> withdraw =
                transaction ->
                        transaction.getTransactionType() == TransactionType.WITHDRAW;

        Predicate<Transactions> transfer =
                transaction ->
                        transaction.getTransactionType() == TransactionType.TRANSFER_DEBIT;

        return DailyAuditReportDTO.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .generatedAt(LocalDateTime.now())

                .totalTransactions(transactions.size())

                .totalDeposits(countTransactions(transactions, deposit))
                .totalWithdrawals(countTransactions(transactions, withdraw))
                .totalTransfers(countTransactions(transactions, transfer))

                .totalDepositAmount(sumTransactions(transactions, deposit))
                .totalWithdrawalAmount(sumTransactions(transactions, withdraw))
                .totalTransferAmount(sumTransactions(transactions, transfer))

                .transactions(auditTransactions)
                .build();
    }

    private TransactionAuditDTO convertToAuditDTO(
            Transactions transaction
    ) {

        return TransactionAuditDTO.builder()
                .transactionId(transaction.getTransactionId())
                .referenceId(transaction.getReferenceId())
                .customerName(transaction.getAccount().getUser().getName())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .accountType(transaction.getAccount().getAccountType())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .transactionTime(transaction.getTransactionTime())
                .build();
    }

    private int countTransactions(
            List<Transactions> transactions,
            Predicate<Transactions> filter
    ) {

        return (int) transactions.stream()
                .filter(filter)
                .count();
    }

    private BigDecimal sumTransactions(
            List<Transactions> transactions,
            Predicate<Transactions> filter
    ) {

        return transactions.stream()
                .filter(filter)
                .map(Transactions::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatReportAsEmailBody(DailyAuditReportDTO report) {
        return String.format("""
                Daily Audit Report
                ==================
                Report Date: %s to %s
                Generated At: %s
                
                Transaction Summary:
                - Total Transactions: %d
                - Total Deposits: %d
                - Total Withdrawals: %d
                - Total Transfers: %d
                
                Amount Summary:
                - Total Deposit Amount: %s
                - Total Withdrawal Amount: %s
                - Total Transfer Amount: %s
                """,
                report.getFromDate(),
                report.getToDate(),
                report.getGeneratedAt(),
                report.getTotalTransactions(),
                report.getTotalDeposits(),
                report.getTotalWithdrawals(),
                report.getTotalTransfers(),
                report.getTotalDepositAmount(),
                report.getTotalWithdrawalAmount(),
                report.getTotalTransferAmount()
        );
    }

}