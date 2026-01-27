package iuh.fit.se.tramcamxuc.modules.payment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.modules.payment.dto.PayOSWebhookDTO;
import iuh.fit.se.tramcamxuc.modules.payment.entity.PaymentTransaction;
import iuh.fit.se.tramcamxuc.modules.payment.repository.PaymentTransactionRepository;
import iuh.fit.se.tramcamxuc.modules.payment.service.PaymentService;
import iuh.fit.se.tramcamxuc.modules.subscription.entity.SubscriptionPlan;
import iuh.fit.se.tramcamxuc.modules.subscription.repository.SubscriptionPlanRepository;
import iuh.fit.se.tramcamxuc.modules.subscription.service.SubscriptionService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import iuh.fit.se.tramcamxuc.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.type.*;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PayOS payOS;
    private final PaymentTransactionRepository transactionRepo;
    private final SubscriptionPlanRepository planRepo;
    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    @Value("${payos.return-url}")
    private String returnUrl;
    @Value("${payos.cancel-url}")
    private String cancelUrl;

    @Override
    @Transactional
    public CheckoutResponseData createPaymentLink(UUID planId) {
        User user = userService.getCurrentUser();
        SubscriptionPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Gói cước không tồn tại"));

        long orderCode = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(3));

        String desc = "Mua " + plan.getName();
        if (desc.length() > 25) desc = desc.substring(0, 25);

        ItemData item = ItemData.builder()
                .name(plan.getName())
                .quantity(1)
                .price(plan.getPrice().intValue())
                .build();

        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(plan.getPrice().intValue())
                .description(desc)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .item(item)
                .build();

        try {
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            PaymentTransaction trans = PaymentTransaction.builder()
                    .orderCode(orderCode)
                    .amount(plan.getPrice())
                    .user(user)
                    .plan(plan)
                    .status("PENDING")
                    .description(desc)
                    .build();
            transactionRepo.save(trans);

            return data;

        } catch (Exception e) {
            throw new AppException("Lỗi tạo link thanh toán: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public WebhookData handleWebhook(PayOSWebhookDTO dto) {
        try {
            // 1. Convert Map -> WebhookData (Bypass lỗi constructor)
            WebhookData webhookData = objectMapper.convertValue(dto.getData(), WebhookData.class);

            boolean isSuccess = dto.getSuccess() != null ? dto.getSuccess() : "00".equals(dto.getCode());

            // 2. Build Webhook Object chuẩn SDK
            Webhook webhook = Webhook.builder()
                    .code(dto.getCode())
                    .desc(dto.getDesc())
                    .success(isSuccess)
                    .data(webhookData)
                    .signature(dto.getSignature())
                    .build();

            // 3. Verify Signature
            WebhookData verifiedData = payOS.verifyPaymentWebhookData(webhook);

            // 4. Xử lý nghiệp vụ
            PaymentTransaction trans = transactionRepo.findByOrderCode(verifiedData.getOrderCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

            if ("SUCCESS".equals(trans.getStatus())) return verifiedData;

            if ("00".equals(verifiedData.getCode())) {
                trans.setStatus("SUCCESS");
                trans.setPaymentDate(new Date());

                // >>> GỌI HÀM KÍCH HOẠT VỪA VIẾT <<<
                subscriptionService.activateSubscription(trans.getUser(), trans.getPlan());

                log.info("Thanh toán thành công đơn hàng: " + verifiedData.getOrderCode());
            } else {
                trans.setStatus("FAILED");
            }
            transactionRepo.save(trans);
            return verifiedData;

        } catch (Exception e) {
            log.error("Webhook Error: ", e);
            return null; // Trả null để Controller return 200 OK
        }
    }
}