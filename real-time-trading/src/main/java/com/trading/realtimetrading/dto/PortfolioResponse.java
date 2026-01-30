package com.trading.realtimetrading.dto;

import com.trading.realtimetrading.domain.portfolio.Portfolio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponse {
    private Long portfolioId;
    private String stockCode;
    private Long quantity;
    private Double averagePrice;
    private Double totalInvestment;
    private Double currentPrice;
    private Double currentValue;
    private Double profitLoss;
    private Double profitRate;

    public static PortfolioResponse from(Portfolio portfolio, Double currentPrice) {
        double currentValue = portfolio.calculateCurrentValue(currentPrice);
        double profitLoss = currentValue - portfolio.getTotalInvestment();
        double profitRate = portfolio.calculateProfitRate(currentPrice);

        return PortfolioResponse.builder()
                .portfolioId(portfolio.getId())
                .stockCode(portfolio.getStockCode())
                .quantity(portfolio.getQuantity())
                .averagePrice(portfolio.getAveragePrice())
                .totalInvestment(portfolio.getTotalInvestment())
                .currentPrice(currentPrice)
                .currentValue(currentValue)
                .profitLoss(profitLoss)
                .profitRate(profitRate)
                .build();
    }
}
