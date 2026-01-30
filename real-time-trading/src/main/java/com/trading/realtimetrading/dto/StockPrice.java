package com.trading.realtimetrading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice {
    private String code;
    private Double price;
    private Long timestamp;
}