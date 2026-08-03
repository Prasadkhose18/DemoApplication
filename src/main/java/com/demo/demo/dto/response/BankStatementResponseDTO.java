package com.demo.demo.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BankStatementResponseDTO {

    private String accountNumber;
    private String accountType;
    private String holderName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDateTime generatedAt;
    private int totalTransactions;
    private List<TransactionStatementDTO> transactions;
}
