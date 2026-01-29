package com.trading.realtimetrading;

import com.trading.realtimetrading.external.UpbitWebSocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class RealTimeTradingApplication implements CommandLineRunner {

    private final UpbitWebSocketClient upbitClient;

    public static void main(String[] args) {
        SpringApplication.run(RealTimeTradingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 업비트 WebSocket 연결
        upbitClient.connect();
    }
}