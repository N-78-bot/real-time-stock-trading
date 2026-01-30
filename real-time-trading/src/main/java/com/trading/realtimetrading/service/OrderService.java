package com.trading.realtimetrading.service;

import com.trading.realtimetrading.domain.order.Order;
import com.trading.realtimetrading.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 분산 락을 위한 상수
    private static final String LOCK_PREFIX = "lock:order:";
    private static final long LOCK_TIMEOUT = 10; // 10초

    /**
     * 주문 생성 (분산 락 적용)
     */
    @Transactional
    public Order createOrder(Order order) {
        Long userId = order.getUser().getId();
        String lockKey = LOCK_PREFIX + userId;

        try {
            // 락 획득 시도
            if (acquireLock(lockKey)) {
                try {
                    // 주문 생성 로직
                    log.info("주문 생성 - 사용자: {}, 종목: {}, 타입: {}",
                            userId, order.getStockCode(), order.getOrderType());
                    return orderRepository.save(order);
                } finally {
                    // 락 해제
                    releaseLock(lockKey);
                }
            } else {
                throw new RuntimeException("다른 주문이 처리 중입니다. 잠시 후 다시 시도해주세요.");
            }
        } catch (Exception e) {
            log.error("주문 생성 실패 - 사용자: {}", userId, e);
            throw e;
        }
    }

    /**
     * 주문 조회
     */
    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. ID: " + orderId));
    }

    /**
     * 사용자별 주문 내역 조회
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 사용자의 특정 종목 주문 내역 조회
     */
    @Transactional(readOnly = true)
    public List<Order> getUserOrdersByStock(Long userId, String stockCode) {
        return orderRepository.findByUserIdAndStockCodeOrderByCreatedAtDesc(userId, stockCode);
    }

    /**
     * 주문 체결 완료 처리
     */
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = getOrder(orderId);
        order.complete();
        orderRepository.save(order);
        log.info("주문 체결 완료 - ID: {}", orderId);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrder(orderId);
        order.cancel();
        orderRepository.save(order);
        log.info("주문 취소 - ID: {}", orderId);
    }

    /**
     * Redis SETNX를 이용한 분산 락 획득
     */
    private boolean acquireLock(String lockKey) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", LOCK_TIMEOUT, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 락 해제
     */
    private void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}