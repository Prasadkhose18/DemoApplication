package com.demo.demo.controller;

import com.demo.demo.dto.ApiResponse;
import com.demo.demo.dto.TransactionRequestDTO;
import com.demo.demo.dto.TransactionResponseDTO;
import com.demo.demo.entity.Transactions;
import com.demo.demo.mapper.TransactionMapper;
import com.demo.demo.service.TransactionService;
import com.demo.demo.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/transaction")
public class TransactionsController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final ResponseBuilder responseBuilder;

    public TransactionsController(TransactionService transactionService,
                                  TransactionMapper transactionMapper,
                                  ResponseBuilder responseBuilder) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
        this.responseBuilder = responseBuilder;
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> deposit(
            @Valid @RequestBody TransactionRequestDTO request,
            HttpServletRequest httpRequest) {

        log.info("Deposit request received for account {}", request.getAccountNumber());

        Transactions transaction = transactionService.deposit(
                request.getAccountNumber(),
                request.getAmount());

        log.info("Deposit completed. Reference ID: {}", transaction.getReferenceId());

        return responseBuilder.created(
                "Deposit successful",
                transactionMapper.toResponseDTO(transaction),
                httpRequest.getRequestURI()
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponseDTO>> withdraw(
            @Valid @RequestBody TransactionRequestDTO request,
            HttpServletRequest httpRequest) {

        log.info("Withdrawal request received for account {}", request.getAccountNumber());

        Transactions transaction = transactionService.withdraw(
                request.getAccountNumber(),
                request.getAmount());

        log.info("Withdrawal completed. Reference ID: {}", transaction.getReferenceId());

        return responseBuilder.created(
                "Withdrawal successful",
                transactionMapper.toResponseDTO(transaction),
                httpRequest.getRequestURI()
        );
    }
}