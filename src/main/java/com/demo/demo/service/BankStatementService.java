package com.demo.demo.service;

import com.demo.demo.dto.response.BankStatementResponseDTO;

import java.time.LocalDate;

public interface BankStatementService {

    BankStatementResponseDTO generateStatement(String accountNumber,
                                               LocalDate fromDate,
                                               LocalDate toDate);
}
