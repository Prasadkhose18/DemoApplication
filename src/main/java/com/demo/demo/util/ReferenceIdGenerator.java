package com.demo.demo.util;

import com.demo.demo.enums.ReferenceType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReferenceIdGenerator {

    public String generate(ReferenceType referenceType) {

        return referenceType.getPrefix()
                + "-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}