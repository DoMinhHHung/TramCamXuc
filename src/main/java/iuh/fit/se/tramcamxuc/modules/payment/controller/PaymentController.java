package iuh.fit.se.tramcamxuc.modules.payment.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.payment.dto.PayOSWebhookDTO;
import iuh.fit.se.tramcamxuc.modules.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// >>> SỬA IMPORT: Dùng class Response mới của V2 <<<
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-link/{planId}")
    public ResponseEntity<ApiResponse<CreatePaymentLinkResponse>> createLink(@PathVariable UUID planId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.createPaymentLink(planId)));
    }

    // Webhook giữ nguyên
    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> webhook(@RequestBody PayOSWebhookDTO webhookBody) {
        paymentService.handleWebhook(webhookBody);
        return ResponseEntity.ok(ApiResponse.success("Webhook received"));
    }
}