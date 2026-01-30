package com.trading.realtimetrading.domain.order;

import com.trading.realtimetrading.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String stockCode;  // 주식 코드

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderType orderType;  // 매수/매도

    @Column(nullable = false)
    private Long quantity;  // 수량

    @Column(nullable = false)
    private Double price;  // 주문 가격

    @Column(nullable = false)
    private Double totalAmount;  // 총 금액 (수량 * 가격)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;  // 주문 상태

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime completedAt;  // 체결 시간

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        totalAmount = quantity * price;
    }

    /**
     * 주문 체결 처리
     */
    public void complete() {
        this.status = OrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * 주문 취소
     */
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}
