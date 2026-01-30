package com.trading.realtimetrading.service;

import com.trading.realtimetrading.domain.portfolio.Portfolio;
import com.trading.realtimetrading.domain.portfolio.PortfolioRepository;
import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.dto.PortfolioResponse;
import com.trading.realtimetrading.dto.StockPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StockService stockService;

    /**
     * 사용자 포트폴리오 조회
     */
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getUserPortfolio(User user) {
        List<Portfolio> portfolios = portfolioRepository.findByUser(user);

        return portfolios.stream()
                .map(portfolio -> {
                    // 현재 시세 조회
                    StockPrice stockPrice = stockService.getStockPrice(portfolio.getStockCode());
                    return PortfolioResponse.from(portfolio, stockPrice.getPrice());
                })
                .collect(Collectors.toList());
    }
}
