package iuh.fit.se.tramcamxuc.modules.payment.service;

import iuh.fit.se.tramcamxuc.modules.payment.dto.PayOSWebhookDTO;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.Webhook;
import vn.payos.type.WebhookData;

import java.util.UUID;

public interface PaymentService {
    CheckoutResponseData createPaymentLink(UUID planId);

    WebhookData handleWebhook(PayOSWebhookDTO dto);
}