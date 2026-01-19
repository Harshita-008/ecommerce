package com.example.ecommerce.webhook;

import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentService paymentService;

    // RAZORPAY WEBHOOK
    @PostMapping("/payment")
    public void razorpayWebhook(@RequestBody Map<String, Object> payload) {

        Object payloadObj = payload.get("payload");
        if (!(payloadObj instanceof Map)) {
            return;
        }

        Map<?, ?> payloadMap = (Map<?, ?>) payloadObj;

        Object paymentObj = payloadMap.get("payment");
        if (!(paymentObj instanceof Map)) {
            return;
        }

        Map<?, ?> paymentMap = (Map<?, ?>) paymentObj;

        Object entityObj = paymentMap.get("entity");
        if (!(entityObj instanceof Map)) {
            return;
        }

        Map<?, ?> entity = (Map<?, ?>) entityObj;

        String paymentId = (String) entity.get("id");
        String status = (String) entity.get("status");
        String orderId = (String) entity.get("receipt");

        if ("captured".equals(status)) {
            paymentService.markPaymentSuccess(orderId, paymentId);
        }
    }

}
