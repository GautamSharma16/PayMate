package com.gautam.billingsoftware.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private String orderId;
    private String customerName;
    private String phoneNumber;
    private List<OrderResponse.OrderItemResponse> item;
    private Double subTotal;
    private Double tax;
    private Double grandTotal;
    private PaymentMethod paymentMethod;
    private LocalDate createdAt;
    private PaymentDetails paymentDetails;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderItemResponse
    {
        private String itemId;
        private String name;
        private Double price;
        private Integer quantity;
    }
}
