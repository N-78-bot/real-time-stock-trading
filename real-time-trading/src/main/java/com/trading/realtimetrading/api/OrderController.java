package com.trading.realtimetrading.api;

import com.trading.realtimetrading.domain.order.Order;
import com.trading.realtimetrading.domain.order.OrderStatus;
import com.trading.realtimetrading.domain.order.OrderType;
import com.trading.realtimetrading.domain.order.PriceType;
import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.dto.OrderRequest;
import com.trading.realtimetrading.dto.OrderResponse;
import com.trading.realtimetrading.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 매수 주문
     */
    @PostMapping("/buy")
    public ResponseEntity<OrderResponse> buyOrder(@Valid @RequestBody OrderRequest request) {
        log.info("매수 주문 요청: {}", request.getStockCode());

        // 테스트용 User 객체 생성 (실제로는 인증된 사용자 정보를 사용)
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("테스트유저")
                .balance(new BigDecimal("10000000"))
                .build();

        // 가격 타입 결정
        PriceType priceType = request.getPrice() != null ? PriceType.LIMIT : PriceType.MARKET;

        // 총 금액 계산 (시장가는 null, 지정가는 계산)
        BigDecimal totalAmount = null;
        if (priceType == PriceType.LIMIT && request.getPrice() != null) {
            totalAmount = request.getPrice().multiply(request.getQuantity());
        }

        Order order = Order.builder()
                .user(user)
                .stockCode(request.getStockCode())
                .orderType(OrderType.BUY)
                .priceType(priceType)
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(OrderResponse.from(savedOrder));
    }

    /**
     * 매도 주문
     */
    @PostMapping("/sell")
    public ResponseEntity<OrderResponse> sellOrder(@Valid @RequestBody OrderRequest request) {
        log.info("매도 주문 요청: {}", request.getStockCode());

        // 테스트용 User 객체 생성
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .nickname("테스트유저")
                .balance(new BigDecimal("10000000"))
                .build();

        PriceType priceType = request.getPrice() != null ? PriceType.LIMIT : PriceType.MARKET;

        BigDecimal totalAmount = null;
        if (priceType == PriceType.LIMIT && request.getPrice() != null) {
            totalAmount = request.getPrice().multiply(request.getQuantity());
        }

        Order order = Order.builder()
                .user(user)
                .stockCode(request.getStockCode())
                .orderType(OrderType.SELL)
                .priceType(priceType)
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .build();

        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.ok(OrderResponse.from(savedOrder));
    }

    /**
     * 주문 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    /**
     * 사용자별 주문 내역 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 사용자의 특정 종목 주문 내역 조회
     */
    @GetMapping("/user/{userId}/stock/{stockCode}")
    public ResponseEntity<List<OrderResponse>> getUserOrdersByStock(
            @PathVariable Long userId,
            @PathVariable String stockCode) {
        List<Order> orders = orderService.getUserOrdersByStock(userId, stockCode);
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 주문 취소
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        log.info("주문 취소 요청: {}", orderId);
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 주문 체결 완료 (테스트용)
     */
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(@PathVariable Long orderId) {
        log.info("주문 체결 요청: {}", orderId);
        orderService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }
}