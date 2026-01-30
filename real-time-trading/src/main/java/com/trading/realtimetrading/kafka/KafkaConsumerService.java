package com.trading.realtimetrading.kafka;

import com.trading.realtimetrading.dto.StockPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "market.price.updates", groupId = "trading-system")
    public void consume(StockPrice price) {
        log.info("Received: {}", price);
    }
}