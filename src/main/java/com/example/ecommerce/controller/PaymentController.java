package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // RAZORPAY PAYMENT INIT
    @PostMapping("/razorpay/create")
    public Map<String, Object> createRazorpayPayment(
            @RequestBody PaymentRequest request) {

        Payment payment = paymentService.createRazorpayPayment(request);

        return Map.of(
                "orderId", payment.getOrderId(),
                "amount", payment.getAmount(),
                "status", payment.getStatus()
        );
    }
}
