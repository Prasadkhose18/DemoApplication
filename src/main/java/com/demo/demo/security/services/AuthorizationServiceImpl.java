package com.demo.demo.security.services;

import com.demo.demo.entity.Accounts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final CurrentUserService currentUserService;

    public AuthorizationServiceImpl(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    public void checkAccountAccess(Accounts account) {

        // Admin can access every account
        if (currentUserService.isAdmin()) {

            log.debug("Admin {} is accessing account {}",
                    currentUserService.getCurrentUserEmail(),
                    account.getAccountNumber());

            return;
        }

        Long currentUserId = currentUserService.getCurrentUserId();
        Long ownerId = account.getUser().getUserId();

        if (!currentUserId.equals(ownerId)) {

            log.warn(
                    "Access denied. User {} attempted to access account {} owned by user {}",
                    currentUserId,
                    account.getAccountNumber(),
                    ownerId
            );

            throw new AccessDeniedException(
                    "You are not authorized to access this account."
            );
        }

        log.debug(
                "User {} authorized to access account {}",
                currentUserId,
                account.getAccountNumber()
        );
    }
}