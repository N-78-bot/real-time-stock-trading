package com.trading.realtimetrading.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequest {
    
    @NotNull(message = "금액은 필수입니다")
    @Min(value = 1000, message = "최소 금액은 1,000원입니다")
    private BigDecimal amount;
}
