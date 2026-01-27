package iuh.fit.se.tramcamxuc.modules.payment.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
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

    @Value("${payos.return-url}")
    private String returnUrl;
    @Value("${payos.cancel-url}")
    private String cancelUrl;

    @Override
    @Transactional
    public CheckoutResponseData createPaymentLink(UUID planId) {
        User user = userService.getCurrentUser();
        SubscriptionPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

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
                //.buyerName(user.getName()) // Optional
                //.buyerEmail(user.getEmail()) // Optional
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
            log.error("PayOS Error: ", e);
            throw new AppException("Lỗi tạo link thanh toán PayOS");
        }
    }

    @Override
    @Transactional
    public WebhookData handleWebhook(Webhook webhookBody) {
        try {
            WebhookData data = payOS.verifyPaymentWebhookData(webhookBody);

            PaymentTransaction trans = transactionRepo.findByOrderCode(data.getOrderCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

            if ("SUCCESS".equals(trans.getStatus())) {
                return data;
            }

            if ("00".equals(data.getCode())) {
                trans.setStatus("SUCCESS");
                trans.setPaymentDate(new Date());

                subscriptionService.activateSubscription(trans.getUser(), trans.getPlan());

                log.info("Activate subscription for user: {}", trans.getUser().getEmail());
            } else {
                trans.setStatus("FAILED");
            }

            transactionRepo.save(trans);
            return data;

        } catch (Exception e) {
            log.error("Webhook Error: ", e);
            throw new AppException("Lỗi xử lý Webhook");
        }
    }
}