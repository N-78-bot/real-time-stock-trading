package com.trading.realtimetrading.dto;

import com.trading.realtimetrading.domain.order.OrderType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotBlank(message = "주식 코드는 필수입니다")
    private String stockCode;

    @NotNull(message = "주문 타입은 필수입니다")
    private OrderType orderType;

    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
    private Long quantity;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    private Double price;
}
