package com.trading.realtimetrading.controller;

import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.domain.user.UserRepository;
import com.trading.realtimetrading.dto.PortfolioResponse;
import com.trading.realtimetrading.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final UserRepository userRepository;

    /**
     * 보유 주식 조회
     * GET /api/portfolio
     */
    @GetMapping
    public List<PortfolioResponse> getPortfolio(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        return portfolioService.getUserPortfolio(user);
    }
}
