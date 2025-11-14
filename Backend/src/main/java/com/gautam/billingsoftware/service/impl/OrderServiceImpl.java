package com.gautam.billingsoftware.service.impl;

import com.gautam.billingsoftware.entity.OrderEntity;
import com.gautam.billingsoftware.entity.OrderItemEntity;
import com.gautam.billingsoftware.io.*;
import com.gautam.billingsoftware.repository.OrderEntityRepository;
import com.gautam.billingsoftware.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private  final OrderEntityRepository orderEntityRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        OrderEntity newOrder = convertToOrderEntity(request);

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setStatus(newOrder.getPaymentMethod()== PaymentMethod.CASH ?
                PaymentDetails.PaymentStatus.COMPLETED: PaymentDetails.PaymentStatus.PENDING);
        newOrder.setPaymentDetails(paymentDetails);

       List<OrderItemEntity> orderItems = request.getCartItems().stream()
                .map(this::convertToOrderItemEntity)
                .collect(Collectors.toList());
        newOrder.setItems(orderItems);
        newOrder=orderEntityRepository.save(newOrder);
        return convertToResponse(newOrder);
    }

    private OrderItemEntity convertToOrderItemEntity(OrderRequest.OrderItemRequest orderItemRequest) {
         return  OrderItemEntity.builder()
                 .itemId(orderItemRequest.getItemId())
                 .name(orderItemRequest.getName())
                 .price(orderItemRequest.getPrice())
                 .quantity(orderItemRequest.getQuantity())
                 .build();
    }

    private OrderResponse convertToResponse(OrderEntity newOrder) {
       return OrderResponse.builder()
                .orderId(newOrder.getOrderId())
                .customerName(newOrder.getCustomerName())
                .phoneNumber(newOrder.getPhoneNumber())
                .subTotal(newOrder.getSubtotal())
                .tax(newOrder.getTax())
                .grandTotal(newOrder.getGrandTotal())
               .paymentMethod(newOrder.getPaymentMethod())
                .item(newOrder.getItems().stream().
                        map(this::convertToItemResponse)
                        .collect(Collectors.toList()))
                .paymentDetails(newOrder.getPaymentDetails())
                .createdAt(LocalDate.from(newOrder.getCreatedAt()))
                .build();

    }

    private OrderResponse.OrderItemResponse convertToItemResponse(OrderItemEntity orderItemEntity) {
        return OrderResponse.OrderItemResponse.builder()
                .itemId(orderItemEntity.getItemId())
                .name(orderItemEntity.getName())
                .price(orderItemEntity.getPrice())
                .quantity(orderItemEntity.getQuantity())
                .build();

    }

    private OrderEntity convertToOrderEntity(OrderRequest request) {
       return OrderEntity.builder()
                .customerName(request.getCustomerName())
                .phoneNumber(request.getPhoneNumber())
                .subtotal(request.getSubTotal())
                .tax(request.getTax())
                .grandTotal(request.getGrandTotal())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()))
                .build();
    }

    @Override
    public void deleteOrder(String orderId) {
        OrderEntity existingOrder = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(()-> new RuntimeException("order not found"));
                 orderEntityRepository.delete(existingOrder);
    }

    @Override
    public List<OrderResponse> getLatestorders() {
        return orderEntityRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse verifyPayment(PaymentVerificationRequest request) {
        OrderEntity existingOrder = orderEntityRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("order not found"));

        if(!verifyRazorpaySignature(request.getRazorpayPaymentId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature())) {
            throw new RuntimeException("Razorpay signature verification failed");
        }

        PaymentDetails paymentDetails = existingOrder.getPaymentDetails();
        paymentDetails.setRazorpayOrderId(existingOrder.getOrderId());
        paymentDetails.setRazorpayPaymentId(request.getRazorpayPaymentId());
        paymentDetails.setRazorpaySignature(request.getRazorpaySignature());


        paymentDetails.setStatus(PaymentDetails.PaymentStatus.COMPLETED);

        existingOrder = orderEntityRepository.save(existingOrder);
        return convertToResponse(existingOrder);
    }

    @Override
    public Double sumSalesByDate(LocalDate date) {
        return orderEntityRepository.sumSalesByDate(date);
    }

    @Override
    public Long countByOrderDate(LocalDate date) {
        return orderEntityRepository.countByOrderDate(date);
    }

    @Override
    public OrderEntity getOrderById(String orderId) {
        try {

            if (orderId.matches("\\d+")) {
                Long id = Long.parseLong(orderId);
                return orderEntityRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            } else {

                return orderEntityRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new RuntimeException("Order not found with orderId: " + orderId));
            }
        } catch (NumberFormatException e) {

            return orderEntityRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with orderId: " + orderId));
        }

    }

    private boolean verifyRazorpaySignature(String razorpayPaymentId, String razorpayPaymentId1, String razorpaySignature) {
        return true;
    }
}
