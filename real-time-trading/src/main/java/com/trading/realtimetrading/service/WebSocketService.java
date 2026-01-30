package com.trading.realtimetrading.service;

import com.trading.realtimetrading.dto.StockPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket 메시지 전송 서비스
 * - 실시간 시세를 구독 중인 클라이언트에게 브로드캐스트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 특정 주식 코드 구독자들에게 시세 전송
     * Topic: /topic/stock/{code}
     */
    public void sendStockPrice(StockPrice stockPrice) {
        String destination = "/topic/stock/" + stockPrice.getCode();
        messagingTemplate.convertAndSend(destination, stockPrice);
        log.info("Sent to WebSocket {}: {}", destination, stockPrice);
    }

    /**
     * 모든 시세 구독자들에게 전송
     * Topic: /topic/stocks/all
     */
    public void broadcastStockPrice(StockPrice stockPrice) {
        messagingTemplate.convertAndSend("/topic/stocks/all", stockPrice);
        log.info("Broadcast to all: {}", stockPrice);
    }
}
