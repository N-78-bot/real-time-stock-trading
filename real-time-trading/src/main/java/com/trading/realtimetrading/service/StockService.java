package com.trading.realtimetrading.service;

import com.trading.realtimetrading.domain.stock.Stock;
import com.trading.realtimetrading.domain.stock.StockRepository;
import com.trading.realtimetrading.dto.StockPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockPriceCache stockPriceCache;
    private final WebSocketService webSocketService;

    /**
     * 실시간 시세 업데이트 처리
     * 1. DB에 저장
     * 2. Redis 캐시 업데이트
     * 3. WebSocket으로 브로드캐스트
     */
    @Transactional
    public void updateStockPrice(StockPrice stockPrice) {
        Stock stock = stockRepository.findByCode(stockPrice.getCode())
                .orElseGet(() -> createNewStock(stockPrice));

        // 가격 업데이트
        stock.updatePrice(stockPrice.getPrice());
        stockRepository.save(stock);

        // Redis 캐시 업데이트
        stockPriceCache.cachePrice(stockPrice);

        // WebSocket으로 실시간 전송
        webSocketService.sendStockPrice(stockPrice);
        webSocketService.broadcastStockPrice(stockPrice);

        log.info("Updated stock: {} - Price: {}", stockPrice.getCode(), stockPrice.getPrice());
    }

    /**
     * 새로운 주식 생성
     */
    private Stock createNewStock(StockPrice stockPrice) {
        return Stock.builder()
                .code(stockPrice.getCode())
                .name(stockPrice.getCode()) // 임시로 코드를 이름으로 사용
                .currentPrice(stockPrice.getPrice())
                .volume(0L)
                .build();
    }

    /**
     * 주식 시세 조회 (캐시 우선)
     */
    @Transactional(readOnly = true)
    public StockPrice getStockPrice(String code) {
        // 1. 캐시에서 먼저 조회
        StockPrice cached = stockPriceCache.getPrice(code);
        if (cached != null) {
            return cached;
        }

        // 2. 캐시 미스 시 DB에서 조회
        Stock stock = stockRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("주식을 찾을 수 없습니다: " + code));

        StockPrice stockPrice = new StockPrice(
                stock.getCode(),
                stock.getCurrentPrice(),
                System.currentTimeMillis()
        );

        // 3. 캐시에 저장
        stockPriceCache.cachePrice(stockPrice);

        return stockPrice;
    }
}
