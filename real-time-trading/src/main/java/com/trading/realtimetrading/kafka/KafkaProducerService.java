package com.trading.realtimetrading.kafka;

import com.trading.realtimetrading.dto.StockPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, StockPrice> kafkaTemplate;

    public void sendPrice(StockPrice price) {
        kafkaTemplate.send("market.price.updates", price.getCode(), price);
        log.info("Sent: {}", price);
    }
}