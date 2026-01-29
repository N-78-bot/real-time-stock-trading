package com.trading.realtimetrading.external;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StockPricePublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final Gson gson = new Gson();

    public void publish(String code, Double price, Double changeRate) {
        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        data.put("price", price);
        data.put("changeRate", changeRate);
        data.put("timestamp", System.currentTimeMillis());

        String message = gson.toJson(data);
        redisTemplate.convertAndSend("stock-price", message);
    }
}