package com.trading.realtimetrading.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // 사용자별 주문 내역 조회
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 종목의 주문 내역
    List<Order> findByUserIdAndStockCodeOrderByCreatedAtDesc(Long userId, String stockCode);
}