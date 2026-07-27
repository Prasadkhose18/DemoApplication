package com.demo.demo.service;

import com.demo.demo.entity.User;

public interface CurrentUserService {

    String getEmail();

    User getCurrentUser();
}
