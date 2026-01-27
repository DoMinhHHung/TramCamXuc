package iuh.fit.se.tramcamxuc.modules.payment.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.Webhook;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-link/{planId}")
    public ResponseEntity<ApiResponse<CheckoutResponseData>> createLink(@PathVariable UUID planId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createPaymentLink(planId)));
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> webhook(@RequestBody Webhook webhookBody) {
        paymentService.handleWebhook(webhookBody);
        return ResponseEntity.ok(ApiResponse.success("Webhook received"));
    }
}