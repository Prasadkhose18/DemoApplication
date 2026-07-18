package com.demo.demo.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequestDTO {

    private String fromAccountNumber;

    private String toAccountNumber;

    private BigDecimal amount;

}
