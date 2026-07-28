package com.demo.demo.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReferenceType {

    TRANSACTION("TXN"),
    ACCOUNT("ACC");

    private final String prefix;
}