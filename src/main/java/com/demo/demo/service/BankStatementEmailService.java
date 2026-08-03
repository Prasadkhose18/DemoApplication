package com.demo.demo.service;

import com.demo.demo.dto.response.BankStatementResponseDTO;

public interface BankStatementEmailService {

    void sendStatement(BankStatementResponseDTO statement, String recipientEmail);
}
