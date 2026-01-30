package com.trading.realtimetrading.dto;

import com.trading.realtimetrading.domain.order.Order;
import com.trading.realtimetrading.domain.order.OrderStatus;
import com.trading.realtimetrading.domain.order.OrderType;
import com.trading.realtimetrading.domain.order.PriceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String stockCode;
    private OrderType orderType;
    private PriceType priceType;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .stockCode(order.getStockCode())
                .orderType(order.getOrderType())
                .priceType(order.getPriceType())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}