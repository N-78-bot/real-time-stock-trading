package com.trading.realtimetrading.domain.stock;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;  // 주식 코드 (예: BTC, ETH, AAPL)

    @Column(nullable = false, length = 100)
    private String name;  // 주식 이름

    @Column(nullable = false)
    private Double currentPrice;  // 현재가

    @Column
    private Double previousClose;  // 전일 종가

    @Column
    private Double changeRate;  // 등락률

    @Column
    private Long volume;  // 거래량

    @Column(nullable = false)
    private LocalDateTime updatedAt;  // 마지막 업데이트 시간

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void updatePrice(Double newPrice) {
        if (this.currentPrice != null) {
            this.previousClose = this.currentPrice;
            this.changeRate = ((newPrice - this.currentPrice) / this.currentPrice) * 100;
        }
        this.currentPrice = newPrice;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateVolume(Long volume) {
        this.volume = volume;
        this.updatedAt = LocalDateTime.now();
    }
}
