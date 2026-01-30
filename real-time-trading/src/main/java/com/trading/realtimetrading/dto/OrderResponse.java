package com.trading.realtimetrading.dto;

import com.trading.realtimetrading.domain.order.Order;
import com.trading.realtimetrading.domain.order.OrderStatus;
import com.trading.realtimetrading.domain.order.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private String stockCode;
    private OrderType orderType;
    private Long quantity;
    private Double price;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .stockCode(order.getStockCode())
                .orderType(order.getOrderType())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .completedAt(order.getCompletedAt())
                .build();
    }
}
