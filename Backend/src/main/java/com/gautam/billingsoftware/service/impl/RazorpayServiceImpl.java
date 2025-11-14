package com.gautam.billingsoftware.service.impl;

import com.gautam.billingsoftware.io.OrderResponse;
import com.gautam.billingsoftware.io.RazorpayOrderResponse;
import com.gautam.billingsoftware.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @Override
    public RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException {
        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpaySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Razorpay expects amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", "order_rcptid_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1);

        Order order = razorpayClient.orders.create(orderRequest);
        return convertToResponse(order);
    }

    private RazorpayOrderResponse convertToResponse(Order order) {
        Object amountObj = order.get("amount");
        Object createdAtObj = order.get("created_at");

        Double amount = null;
        if (amountObj instanceof Integer) {
            amount = ((Integer) amountObj).doubleValue();
        } else if (amountObj instanceof Long) {
            amount = ((Long) amountObj).doubleValue();
        } else if (amountObj instanceof Double) {
            amount = (Double) amountObj;
        }

        java.util.Date createdAt = null;
        if (createdAtObj instanceof Number) {
            createdAt = new java.util.Date(((Number) createdAtObj).longValue() * 1000);
        } else if (createdAtObj instanceof java.util.Date) {
            createdAt = (java.util.Date) createdAtObj;
        }

        return RazorpayOrderResponse.builder()
                .id(order.get("id"))
                .entity((String) order.get("entity"))
                .amount(amount)
                .currency((String) order.get("currency"))
                .status((String) order.get("status"))
                .created_at(createdAt)
                .receipt((String) order.get("receipt"))
                .build();
    }

}
