package com.trading.realtimetrading.service;

import com.trading.realtimetrading.dto.StockPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockPriceCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PRICE_KEY_PREFIX = "stock:price:";
    private static final long CACHE_TTL = 3600; // 1시간

    /**
     * 주식 시세를 Redis에 캐싱
     */
    public void cachePrice(StockPrice stockPrice) {
        String key = PRICE_KEY_PREFIX + stockPrice.getCode();
        redisTemplate.opsForValue().set(key, stockPrice, CACHE_TTL, TimeUnit.SECONDS);
        log.info("Cached price for {}: {}", stockPrice.getCode(), stockPrice.getPrice());
    }

    /**
     * Redis에서 주식 시세 조회
     */
    public StockPrice getPrice(String code) {
        String key = PRICE_KEY_PREFIX + code;
        Object cached = redisTemplate.opsForValue().get(key);
        
        if (cached instanceof StockPrice) {
            log.info("Cache hit for {}", code);
            return (StockPrice) cached;
        }
        
        log.info("Cache miss for {}", code);
        return null;
    }

    /**
     * 캐시 삭제
     */
    public void evictPrice(String code) {
        String key = PRICE_KEY_PREFIX + code;
        redisTemplate.delete(key);
        log.info("Evicted cache for {}", code);
    }
}
