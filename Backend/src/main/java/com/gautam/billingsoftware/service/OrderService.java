package com.gautam.billingsoftware.service;

import com.gautam.billingsoftware.entity.OrderEntity;
import com.gautam.billingsoftware.io.OrderRequest;
import com.gautam.billingsoftware.io.OrderResponse;
import com.gautam.billingsoftware.io.PaymentVerificationRequest;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
   OrderResponse createOrder(OrderRequest request);

   void deleteOrder(String orderId);

   List<OrderResponse> getLatestorders();

    OrderResponse verifyPayment(PaymentVerificationRequest request);

   Double sumSalesByDate(LocalDate date);

   Long countByOrderDate(LocalDate date);

    OrderEntity getOrderById(String orderId);
}
