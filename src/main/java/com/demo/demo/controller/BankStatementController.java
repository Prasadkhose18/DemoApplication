package com.demo.demo.controller;

import com.demo.demo.dto.response.ApiResponse;
import com.demo.demo.dto.response.BankStatementResponseDTO;
import com.demo.demo.service.BankStatementService;
import com.demo.demo.util.APIResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/accounts")
public class BankStatementController {

    private final BankStatementService bankStatementService;
    private final APIResponseBuilder responseBuilder;

    public BankStatementController(BankStatementService bankStatementService,
                                   APIResponseBuilder responseBuilder) {
        this.bankStatementService = bankStatementService;
        this.responseBuilder = responseBuilder;
    }

    @GetMapping("/{accountNumber}/statement")
    public ResponseEntity<ApiResponse<BankStatementResponseDTO>> getStatement(
            @PathVariable String accountNumber,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            HttpServletRequest request) {

        BankStatementResponseDTO statement = bankStatementService.generateStatement(
                accountNumber, fromDate, toDate);

        return responseBuilder.ok(
                "Bank statement generated successfully",
                statement,
                request.getRequestURI()
        );
    }
}
