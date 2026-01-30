package com.trading.realtimetrading.api;

import com.trading.realtimetrading.domain.order.Order;
import com.trading.realtimetrading.domain.order.OrderStatus;
import com.trading.realtimetrading.domain.order.OrderType;
import com.trading.realtimetrading.domain.order.PriceType;
import com.trading.realtimetrading.domain.user.User;
import com.trading.realtimetrading.domain.user.UserService;
import com.trading.realtimetrading.dto.OrderRequest;
import com.trading.realtimetrading.dto.OrderResponse;
import com.trading.realtimetrading.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final UserService userService;

    /**
     * 매수 주문
     */
    @PostMapping("/buy")
    public ResponseEntity<OrderResponse> buyOrder(
            Authentication authentication,
            @Valid @RequestBody OrderRequest request) {

        log.info("매수 주문 요청: {}", request.getStockCode());

        // 인증된 사용자 정보 조회
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

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
    public ResponseEntity<OrderResponse> sellOrder(
            Authentication authentication,
            @Valid @RequestBody OrderRequest request) {

        log.info("매도 주문 요청: {}", request.getStockCode());

        // 인증된 사용자 정보 조회
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

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
    public ResponseEntity<OrderResponse> getOrder(
            Authentication authentication,
            @PathVariable Long orderId) {

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        Order order = orderService.getOrder(orderId);

        // 본인의 주문인지 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 주문만 조회할 수 있습니다");
        }

        return ResponseEntity.ok(OrderResponse.from(order));
    }

    /**
     * 내 주문 내역 조회 (인증된 사용자 본인)
     */
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        List<Order> orders = orderService.getUserOrders(user.getId());
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 내 특정 종목 주문 내역 조회
     */
    @GetMapping("/my-orders/stock/{stockCode}")
    public ResponseEntity<List<OrderResponse>> getMyOrdersByStock(
            Authentication authentication,
            @PathVariable String stockCode) {

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        List<Order> orders = orderService.getUserOrdersByStock(user.getId(), stockCode);
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 주문 취소
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId) {

        log.info("주문 취소 요청: {}", orderId);

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        Order order = orderService.getOrder(orderId);

        // 본인의 주문인지 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 주문만 취소할 수 있습니다");
        }

        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * 주문 체결 완료 (테스트용 - 실제 운영에서는 제거 필요)
     */
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(
            Authentication authentication,
            @PathVariable Long orderId) {

        log.info("주문 체결 요청: {}", orderId);

        String email = authentication.getName();
        User user = userService.getUserByEmail(email);

        Order order = orderService.getOrder(orderId);

        // 본인의 주문인지 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("본인의 주문만 체결할 수 있습니다");
        }

        orderService.completeOrder(orderId);
        return ResponseEntity.ok().build();
    }
}