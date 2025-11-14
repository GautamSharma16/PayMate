package com.gautam.billingsoftware.service;

import com.gautam.billingsoftware.io.RazorpayOrderResponse;
import com.razorpay.RazorpayException;

public interface RazorpayService {
    RazorpayOrderResponse createOrder(Double amount, String currency) throws RazorpayException;
}
