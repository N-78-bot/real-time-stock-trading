package com.trading.realtimetrading.controller;

import com.trading.realtimetrading.dto.StockPrice;
import com.trading.realtimetrading.kafka.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final KafkaProducerService producer;

    @PostMapping("/price")
    public String send(@RequestBody StockPrice price) {
        producer.sendPrice(price);
        return "OK";
    }
}