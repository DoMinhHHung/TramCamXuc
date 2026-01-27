package iuh.fit.se.tramcamxuc.modules.payment.service;

import iuh.fit.se.tramcamxuc.modules.payment.dto.PayOSWebhookDTO;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.webhooks.WebhookData;
import java.util.UUID;

public interface PaymentService {
    CreatePaymentLinkResponse createPaymentLink(UUID planId);

    WebhookData handleWebhook(PayOSWebhookDTO webhookBody);
}