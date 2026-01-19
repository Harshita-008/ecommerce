package com.example.ecommerce.service;

import com.example.ecommerce.client.PaymentServiceClient;
import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PaymentServiceClient paymentServiceClient;

    // RAZORPAY PAYMENT CREATION
    public Payment createRazorpayPayment(PaymentRequest req) {

        Order order = orderService.getOrder(req.getOrderId());

        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("Order not in CREATED state");
        }

        // Call Razorpay Order API
        String razorpayOrderId =
                paymentServiceClient.createRazorpayOrder(
                        req.getAmount(),
                        req.getOrderId()
                );

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setOrderId(req.getOrderId());
        payment.setAmount(req.getAmount());
        payment.setStatus("PENDING");
        payment.setPaymentId(null); // set via webhook
        payment.setCreatedAt(Instant.now());

        paymentRepository.save(payment);

        return payment;
    }

    // WEBHOOK SUCCESS HANDLER
    public void markPaymentSuccess(String orderId, String paymentId) {

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus("SUCCESS");
        payment.setPaymentId(paymentId);
        paymentRepository.save(payment);

        orderService.updateStatus(orderId, "PAID");
    }
}
