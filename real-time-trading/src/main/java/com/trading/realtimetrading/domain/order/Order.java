package com.trading.realtimetrading.domain.order;

import com.trading.realtimetrading.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private String stockCode;  // KRW-BTC, KRW-ETH 등

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderType orderType;  // BUY, SELL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PriceType priceType;  // MARKET, LIMIT

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;  // 수량

    @Column(precision = 20, scale = 2)
    private BigDecimal price;  // 가격 (지정가의 경우)

    @Column(precision = 20, scale = 2)
    private BigDecimal totalAmount;  // 총 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;  // PENDING, COMPLETED, CANCELLED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }
}