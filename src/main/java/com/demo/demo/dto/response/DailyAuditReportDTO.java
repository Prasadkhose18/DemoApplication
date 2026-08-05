package com.demo.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyAuditReportDTO {

    private LocalDate fromDate;

    private LocalDate toDate;

    private LocalDateTime generatedAt;

    private int totalTransactions;

    private int totalDeposits;

    private int totalWithdrawals;

    private int totalTransfers;

    private BigDecimal totalDepositAmount;

    private BigDecimal totalWithdrawalAmount;

    private BigDecimal totalTransferAmount;

    private List<TransactionAuditDTO> transactions;

}