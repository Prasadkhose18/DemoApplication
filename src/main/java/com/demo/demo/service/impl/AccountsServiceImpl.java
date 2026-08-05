package com.demo.demo.service.impl;

import com.demo.demo.entity.Accounts;
import com.demo.demo.entity.User;
import com.demo.demo.factory.AccountFactory;
import com.demo.demo.repository.AccountRepository;
import com.demo.demo.security.services.CurrentUserService;
import com.demo.demo.service.AccountsService;
import com.demo.demo.service.RedisService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountsServiceImpl implements AccountsService {

    private final AccountRepository accountRepository;
    private final AccountFactory accountFactory;
    private final CurrentUserService currentUserService;
    private final RedisService redisService;


    public AccountsServiceImpl(AccountRepository accountRepository,
                               AccountFactory accountFactory,
                               CurrentUserService currentUserService,
                               RedisService redisService) {

        this.accountRepository = accountRepository;
        this.accountFactory = accountFactory;
        this.currentUserService = currentUserService;
        this.redisService = redisService;
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



        // Invalidate existing account cache
        redisService.delete(
                "USER_ACCOUNTS:" + currentUser.getEmail()
        );



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



        String cacheKey =
                "USER_ACCOUNTS:" + currentUser.getEmail();



        List<Accounts> cachedAccounts =
                (List<Accounts>) redisService.get(cacheKey);



        if (cachedAccounts != null) {


            log.info(
                    "Accounts fetched from Redis cache for user {}",
                    currentUser.getEmail()
            );


            return cachedAccounts;
        }




        List<Accounts> accounts =
                accountRepository.findByUserEmail(
                        currentUser.getEmail()
                );



        redisService.save(
                cacheKey,
                accounts,
                30
        );



        log.info(
                "{} account(s) found for user {} and cached",
                accounts.size(),
                currentUser.getEmail()
        );


        return accounts;
    }
}
