package com.demo.demo.service.impl;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.User;
import com.demo.demo.exception.ResourceNotFoundException;
import com.demo.demo.exception.UnauthorizedAccessException;
import com.demo.demo.factory.AccountFactory;
import com.demo.demo.repository.AccountRepository;
import com.demo.demo.security.services.CurrentUserService;
import com.demo.demo.service.AccountsService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountsServiceImpl implements AccountsService {

    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;
    private final CurrentUserService currentUserService;


    public AccountsServiceImpl(AccountRepository accountRepository,
                               AccountFactory accountFactory,
                               CurrentUserService currentUserService) {

        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
        this.currentUserService = currentUserService;
    }



    @Transactional
    @CacheEvict(value = "accounts", allEntries = true)
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




    @Cacheable(value = "accounts", keyGenerator = "userEmailKeyGenerator")
    public List<Accounts> getMyAccounts() {

        User currentUser = currentUserService.getCurrentUser();

        log.info("Fetching accounts for user {}", currentUser.getEmail());

        return accountRepository.findByUserEmail(currentUser.getEmail());
    }

    @Transactional
    @CacheEvict(value = "accounts", allEntries = true)
    public void deleteAccount(String accountNumber) {

        log.info("Attempting to delete account: {}", accountNumber);

        Accounts account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.warn("Account not found for deletion: {}", accountNumber);
                    return new ResourceNotFoundException(
                            "Account not found with number: " + accountNumber);
                });

        User currentUser = currentUserService.getCurrentUser();
        boolean isAdmin = currentUserService.isAdmin();
        boolean isOwner = account.getUser().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) {
            log.warn("Unauthorized attempt to delete account: {}. User: {}",
                    accountNumber, currentUser.getEmail());
            throw new UnauthorizedAccessException(
                    "You do not have permission to delete this account");
        }

        accountRepository.delete(account);

        log.info("Account deleted successfully. Account Number: {}", accountNumber);
    }
}
