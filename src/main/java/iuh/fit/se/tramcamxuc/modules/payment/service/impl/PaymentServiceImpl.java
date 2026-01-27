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
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.*;

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
    public CreatePaymentLinkResponse createPaymentLink(UUID planId) {
        User user = userService.getCurrentUser();
        SubscriptionPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        long orderCode = Long.parseLong(String.valueOf(System.currentTimeMillis()).substring(3));
        String desc = "Mua " + plan.getName();
        if (desc.length() > 25) desc = desc.substring(0, 25);

        // 1. Tạo Item theo chuẩn V2
        PaymentLinkItem item = PaymentLinkItem.builder()
                .name(plan.getName())
                .price((long) plan.getPrice().intValue())
                .quantity(1)
                .build();

        // 2. Tạo Request theo chuẩn V2
        // Demo dùng: CreatePaymentLinkRequest.builder()...item(item)...build()
        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .amount((long) plan.getPrice().intValue())
                .description(desc)
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .item(item) // SDK v2 có hàm .item() tiện hơn list
                .build();

        try {
            // 3. Gọi SDK theo chuẩn V2: payOS.paymentRequests().create()
            CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);

            // Lưu DB (Giữ nguyên logic cũ của mày)
            PaymentTransaction trans = PaymentTransaction.builder()
                    .orderCode(orderCode)
                    .amount(plan.getPrice())
                    .user(user)
                    .plan(plan)
                    .status("PENDING")
                    .description(desc)
                    .build();
            transactionRepo.save(trans);

            return response;

        } catch (Exception e) {
            log.error("PayOS Error: ", e);
            throw new AppException("Lỗi PayOS: " + e.getMessage());
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
            WebhookData verifiedData = payOS.webhooks().verify(webhook);

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
            return null;
        }
    }
}