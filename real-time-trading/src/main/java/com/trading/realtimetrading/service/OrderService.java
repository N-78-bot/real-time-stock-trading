package com.trading.realtimetrading.service;

import com.trading.realtimetrading.domain.account.Account;
import com.trading.realtimetrading.domain.account.AccountRepository;
import com.trading.realtimetrading.domain.order.Order;
import com.trading.realtimetrading.domain.order.OrderRepository;
import com.trading.realtimetrading.domain.order.OrderType;
import com.trading.realtimetrading.domain.portfolio.Portfolio;
import com.trading.realtimetrading.domain.portfolio.PortfolioRepository;
import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.dto.OrderRequest;
import com.trading.realtimetrading.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PortfolioRepository portfolioRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderResponse createOrder(User user, OrderRequest request) {
        // 1. 계좌 조회
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("계좌를 찾을 수 없습니다"));

        // 2. 주문 검증
        validateOrder(user, account, request);

        // 3. 주문 생성
        Order order = Order.builder()
                .user(user)
                .stockCode(request.getStockCode())
                .orderType(request.getOrderType())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .build();

        orderRepository.save(order);

        // 4. 즉시 체결 처리
        processOrder(order, account);

        log.info("Order created: {} {} {} @ {}", 
                order.getOrderType(), order.getStockCode(), order.getQuantity(), order.getPrice());

        return OrderResponse.from(order);
    }

    /**
     * 주문 검증
     */
    private void validateOrder(User user, Account account, OrderRequest request) {
        double totalAmount = request.getQuantity() * request.getPrice();

        if (request.getOrderType() == OrderType.BUY) {
            // 매수: 잔고 확인
            if (!account.hasEnoughBalance(totalAmount)) {
                throw new RuntimeException("잔고가 부족합니다");
            }
        } else {
            // 매도: 보유 수량 확인
            Portfolio portfolio = portfolioRepository.findByUserAndStockCode(user, request.getStockCode())
                    .orElseThrow(() -> new RuntimeException("보유 주식이 없습니다"));

            if (portfolio.getQuantity() < request.getQuantity()) {
                throw new RuntimeException("보유 수량이 부족합니다");
            }
        }
    }

    /**
     * 주문 체결 처리
     */
    private void processOrder(Order order, Account account) {
        if (order.getOrderType() == OrderType.BUY) {
            processBuyOrder(order, account);
        } else {
            processSellOrder(order, account);
        }

        order.complete();
        orderRepository.save(order);
    }

    /**
     * 매수 주문 처리
     */
    private void processBuyOrder(Order order, Account account) {
        // 1. 계좌에서 금액 차감
        account.withdraw(order.getTotalAmount());
        accountRepository.save(account);

        // 2. 포트폴리오 업데이트
        Portfolio portfolio = portfolioRepository
                .findByUserAndStockCode(order.getUser(), order.getStockCode())
                .orElseGet(() -> Portfolio.builder()
                        .user(order.getUser())
                        .stockCode(order.getStockCode())
                        .quantity(0L)
                        .averagePrice(0.0)
                        .totalInvestment(0.0)
                        .build());

        portfolio.buy(order.getQuantity(), order.getPrice());
        portfolioRepository.save(portfolio);

        log.info("Buy order processed: {} {} @ {}", 
                order.getStockCode(), order.getQuantity(), order.getPrice());
    }

    /**
     * 매도 주문 처리
     */
    private void processSellOrder(Order order, Account account) {
        // 1. 포트폴리오에서 주식 차감
        Portfolio portfolio = portfolioRepository
                .findByUserAndStockCode(order.getUser(), order.getStockCode())
                .orElseThrow(() -> new RuntimeException("보유 주식이 없습니다"));

        portfolio.sell(order.getQuantity(), order.getPrice());
        portfolioRepository.save(portfolio);

        // 2. 계좌에 금액 입금
        account.deposit(order.getTotalAmount());
        accountRepository.save(account);

        log.info("Sell order processed: {} {} @ {}", 
                order.getStockCode(), order.getQuantity(), order.getPrice());
    }

    /**
     * 사용자의 주문 내역 조회
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }
}
