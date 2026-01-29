package com.trading.realtimetrading.external;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class UpbitWebSocketClient extends WebSocketClient {

    private final Gson gson = new Gson();
    private final StockPricePublisher publisher;

    // @RequiredArgsConstructor 제거하고 수동 생성자만 사용
    public UpbitWebSocketClient(StockPricePublisher publisher) {
        super(URI.create("wss://api.upbit.com/websocket/v1"));
        this.publisher = publisher;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("업비트 WebSocket 연결 성공");

        // 구독 메시지 전송 (비트코인, 이더리움)
        String subscribeMessage = "[{\"ticket\":\"UNIQUE_TICKET\"},{\"type\":\"ticker\",\"codes\":[\"KRW-BTC\",\"KRW-ETH\"]}]";
        send(subscribeMessage);
        log.info("업비트 시세 구독 요청 전송");
    }

    @Override
    public void onMessage(String message) {
        // 텍스트 메시지는 사용 안 함 (업비트는 바이너리 전송)
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        try {
            // 바이너리를 UTF-8 문자열로 변환
            String message = StandardCharsets.UTF_8.decode(bytes).toString();

            // JSON 파싱
            TickerResponse data = gson.fromJson(message, TickerResponse.class);

            if (data != null && data.getCode() != null) {
                // Redis Pub/Sub으로 발행
                publisher.publish(data.getCode(), data.getTradePrice(), data.getSignedChangeRate());

                log.info("시세 수신: {} - {}원 ({}%)",
                        data.getCode(),
                        data.getTradePrice(),
                        data.getSignedChangeRate() * 100);
            }
        } catch (Exception e) {
            log.error("시세 파싱 에러: {}", e.getMessage(), e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("업비트 WebSocket 연결 종료: {}", reason);
    }

    @Override
    public void onError(Exception ex) {
        log.error("업비트 WebSocket 에러: {}", ex.getMessage(), ex);
    }

    // 업비트 응답 DTO
    static class TickerResponse {
        private String type;
        private String code;
        private Double trade_price;
        private Double signed_change_rate;

        public String getCode() {
            return code;
        }

        public Double getTradePrice() {
            return trade_price;
        }

        public Double getSignedChangeRate() {
            return signed_change_rate != null ? signed_change_rate : 0.0;
        }
    }
}