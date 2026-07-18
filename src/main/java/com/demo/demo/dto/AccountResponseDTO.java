package com.demo.demo.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountResponseDTO {
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String status;
}
