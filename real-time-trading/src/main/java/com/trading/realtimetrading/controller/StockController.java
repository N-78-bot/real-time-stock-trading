package com.trading.realtimetrading.controller;

import com.trading.realtimetrading.dto.StockPrice;
import com.trading.realtimetrading.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    /**
     * 주식 시세 조회
     * GET /api/stocks/{code}/price
     */
    @GetMapping("/{code}/price")
    public StockPrice getStockPrice(@PathVariable String code) {
        return stockService.getStockPrice(code);
    }
}
