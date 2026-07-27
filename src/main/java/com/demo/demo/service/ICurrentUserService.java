package com.demo.demo.service;

import com.demo.demo.entity.User;

public interface ICurrentUserService {

    String getEmail();

    User getCurrentUser();
}
