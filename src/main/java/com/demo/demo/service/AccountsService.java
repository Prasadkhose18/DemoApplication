package com.demo.demo.service;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.User;
import com.demo.demo.factory.AccountFactory;
import com.demo.demo.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountsService {

    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;
    private final CurrentUserService currentUserService;

    public AccountsService(AccountRepository accountRepository,
                           AccountFactory accountFactory,
                           CurrentUserService currentUserService) {

        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
        this.currentUserService = currentUserService;
    }


    @Transactional
    public Accounts createAccount(String accountType) {

        User currentUser = currentUserService.getCurrentUser();

        log.info("Creating {} account for user {}",
                accountType,
                currentUser.getEmail());

        Accounts account = accountFactory.createAccount(
                currentUser,
                accountType
        );

        Accounts savedAccount = accountRepository.save(account);

        log.info(
                "Account created successfully. Account Number: {}, User: {}",
                savedAccount.getAccountNumber(),
                currentUser.getEmail()
        );

        return savedAccount;
    }


    public List<Accounts> getMyAccounts() {

        User currentUser = currentUserService.getCurrentUser();

        log.info("Fetching accounts for user {}",
                currentUser.getEmail());

        List<Accounts> accounts =
                accountRepository.findByUserEmail(currentUser.getEmail());

        log.info(
                "{} account(s) found for user {}",
                accounts.size(),
                currentUser.getEmail()
        );

        return accounts;
    }
}