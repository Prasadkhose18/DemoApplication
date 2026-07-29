package com.demo.demo.security;

import com.demo.demo.entity.User;

public interface CurrentUserService {

    User getCurrentUser();

    Long getCurrentUserId();

    String getCurrentUserEmail();

    boolean isAdmin();
}