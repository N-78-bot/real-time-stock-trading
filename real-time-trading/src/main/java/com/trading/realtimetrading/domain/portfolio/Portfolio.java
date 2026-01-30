package com.trading.realtimetrading.domain.portfolio;

import com.trading.realtimetrading.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String stockCode;  // KRW-BTC, KRW-ETH 등

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;  // 보유 수량

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal avgPrice;  // 평균 매수가

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 매수 처리
     */
    public void buy(BigDecimal buyQuantity, BigDecimal buyPrice) {
        BigDecimal totalCost = this.quantity.multiply(this.avgPrice)
                .add(buyQuantity.multiply(buyPrice));
        this.quantity = this.quantity.add(buyQuantity);
        this.avgPrice = totalCost.divide(this.quantity, 2, RoundingMode.HALF_UP);
    }

    /**
     * 매도 처리
     */
    public void sell(BigDecimal sellQuantity) {
        if (this.quantity.compareTo(sellQuantity) < 0) {
            throw new IllegalArgumentException("보유 수량이 부족합니다");
        }
        this.quantity = this.quantity.subtract(sellQuantity);
    }
}