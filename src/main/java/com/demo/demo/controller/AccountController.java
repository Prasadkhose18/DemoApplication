package com.demo.demo.controller;

import com.demo.demo.dto.AccountRequestDTO;
import com.demo.demo.dto.AccountResponseDTO;
import com.demo.demo.entity.Accounts;
import com.demo.demo.mapper.AccountMapper;
import com.demo.demo.service.AccountsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountsService accountsService;
    private final AccountMapper accountMapper;
    public AccountController(AccountsService accountsService, AccountMapper accountMapper){
        this.accountsService= accountsService;
        this.accountMapper = accountMapper;
    }

    // -- Create--

    @PostMapping("/createAccount")
    public ResponseEntity<AccountResponseDTO> createAccount(
            @Valid @RequestBody AccountRequestDTO requestDTO
            ){

        Accounts account = accountsService.createAccount(requestDTO.getAccountType());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(accountMapper.responseDTO(account));
    }

    @GetMapping("/me")
    public ResponseEntity<List<AccountResponseDTO>> getMyAccounts(){
        List<AccountResponseDTO> response =
                accountsService.getMyAccounts()
                        .stream()
                        .map(accountMapper::responseDTO)
                        .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
