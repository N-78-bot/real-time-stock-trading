package com.trading.realtimetrading.kafka;

import com.trading.realtimetrading.dto.StockPrice;
import com.trading.realtimetrading.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final StockService stockService;
    
    @KafkaListener(topics = "market.price.updates", groupId = "trading-system")
    public void consume(StockPrice price) {
        log.info("Received: {}", price);
        
        // 실시간 시세 처리 (DB 저장 + Redis 캐싱)
        stockService.updateStockPrice(price);
    }
}
