package com.trading.realtimetrading.domain.order;

/**
 * 주문 상태
 * PENDING: 대기 중
 * COMPLETED: 체결 완료
 * CANCELLED: 취소됨
 */
public enum OrderStatus {
    PENDING,
    COMPLETED,
    CANCELLED
}
