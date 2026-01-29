package com.trading.realtimetrading.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody());

        // WebSocket으로 브로드캐스트
        messagingTemplate.convertAndSend("/topic/stock-price", payload);

        log.debug("시세 브로드캐스트 완료: {}", payload);
    }
}