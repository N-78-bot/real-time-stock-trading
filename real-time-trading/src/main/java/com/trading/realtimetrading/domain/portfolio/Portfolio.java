package com.trading.realtimetrading.domain.portfolio;

import com.trading.realtimetrading.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "stock_code"}))
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
    private String stockCode;  // 주식 코드

    @Column(nullable = false)
    private Long quantity;  // 보유 수량

    @Column(nullable = false)
    private Double averagePrice;  // 평균 매수 가격

    @Column(nullable = false)
    private Double totalInvestment;  // 총 투자 금액

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 매수 - 보유 수량 증가
     */
    public void buy(Long quantity, Double price) {
        double newTotalInvestment = this.totalInvestment + (quantity * price);
        long newQuantity = this.quantity + quantity;
        
        this.averagePrice = newTotalInvestment / newQuantity;
        this.quantity = newQuantity;
        this.totalInvestment = newTotalInvestment;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 매도 - 보유 수량 감소
     */
    public void sell(Long quantity, Double price) {
        if (this.quantity < quantity) {
            throw new RuntimeException("보유 수량이 부족합니다");
        }
        
        this.quantity -= quantity;
        this.totalInvestment = this.quantity * this.averagePrice;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 평가 금액 계산
     */
    public double calculateCurrentValue(Double currentPrice) {
        return this.quantity * currentPrice;
    }

    /**
     * 수익률 계산
     */
    public double calculateProfitRate(Double currentPrice) {
        double currentValue = calculateCurrentValue(currentPrice);
        return ((currentValue - totalInvestment) / totalInvestment) * 100;
    }
}
