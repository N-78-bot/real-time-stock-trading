package com.trading.realtimetrading.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountResponse {
    private String email;
    private BigDecimal balance;
    private BigDecimal amount;  // 입출금 금액

    // 잔고 조회용 생성자 추가
    public AccountResponse(String email, BigDecimal balance) {
        this.email = email;
        this.balance = balance;
        this.amount = BigDecimal.ZERO;
    }
}