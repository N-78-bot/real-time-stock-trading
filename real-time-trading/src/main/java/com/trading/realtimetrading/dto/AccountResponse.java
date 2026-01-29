package com.trading.realtimetrading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AccountResponse {
    private String message;
    private BigDecimal balance;
    private BigDecimal amount;
}
