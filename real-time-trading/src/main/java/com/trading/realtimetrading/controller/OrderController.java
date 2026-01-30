package com.trading.realtimetrading.controller;

import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.domain.user.UserRepository;
import com.trading.realtimetrading.dto.OrderRequest;
import com.trading.realtimetrading.dto.OrderResponse;
import com.trading.realtimetrading.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    /**
     * 주문 생성
     * POST /api/orders
     */
    @PostMapping
    public OrderResponse createOrder(
            Authentication authentication,
            @Valid @RequestBody OrderRequest request) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        return orderService.createOrder(user, request);
    }

    /**
     * 주문 내역 조회
     * GET /api/orders
     */
    @GetMapping
    public List<OrderResponse> getOrders(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        return orderService.getUserOrders(user);
    }
}
