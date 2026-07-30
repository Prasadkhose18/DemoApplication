package com.demo.demo.security.services;

import com.demo.demo.entity.Accounts;

public interface AuthorizationService {

    /**
     * Checks whether the current user can access the account.
     * Admins can access every account.
     * Normal users can access only their own accounts.
     */
    void checkAccountAccess(Accounts account);
}