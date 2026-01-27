package iuh.fit.se.tramcamxuc.modules.subscription.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.modules.subscription.dto.request.CreatePlanRequest;
import iuh.fit.se.tramcamxuc.modules.subscription.entity.SubscriptionPlan;
import iuh.fit.se.tramcamxuc.modules.subscription.entity.UserSubscription;
import iuh.fit.se.tramcamxuc.modules.subscription.entity.enums.SubscriptionStatus;
import iuh.fit.se.tramcamxuc.modules.subscription.repository.SubscriptionPlanRepository;
import iuh.fit.se.tramcamxuc.modules.subscription.repository.UserSubscriptionRepository;
import iuh.fit.se.tramcamxuc.modules.subscription.service.SubscriptionService;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPlanRepository planRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Override
    public List<SubscriptionPlan> getAllPlans() {
        return planRepository.findAll();
    }

    @Override
    public SubscriptionPlan getPlanById(UUID id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription plan not found"));
    }

    @Override
    @Transactional
    public SubscriptionPlan createPlan(CreatePlanRequest request) {
        if (planRepository.existsByName(request.getName())) {
            throw new AppException("Subscription plan name already exists");
        }

        SubscriptionPlan plan = SubscriptionPlan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .durationDays(request.getDurationDays())
                .features(request.getFeatures())
                .isActive(true)
                .build();

        return planRepository.save(plan);
    }

    @Override
    @Transactional
    public SubscriptionPlan updatePlan(UUID id, CreatePlanRequest request) {
        SubscriptionPlan plan = getPlanById(id);

        if (!plan.getName().equals(request.getName()) && planRepository.existsByName(request.getName())) {
            throw new AppException("Subscription plan name already exists");
        }

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setPrice(request.getPrice());
        plan.setDurationDays(request.getDurationDays());

        if (request.getFeatures() != null) {
            plan.setFeatures(request.getFeatures());
        }

        return planRepository.save(plan);
    }

    @Override
    @Transactional
    public void toggleActive(UUID id) {
        SubscriptionPlan plan = getPlanById(id);
        plan.setActive(!plan.isActive());
        planRepository.save(plan);
    }

    @Override
    @Transactional
    public void activateSubscription(User user, SubscriptionPlan plan) {
        // 1. Kiểm tra xem user đang có gói nào ACTIVE không
        UserSubscription currentSub = userSubscriptionRepository.findByUserId(user.getId())
                .orElse(null);

        LocalDateTime startDate;
        LocalDateTime endDate;

        if (currentSub != null && currentSub.getStatus() == SubscriptionStatus.ACTIVE) {
            // CASE 1: Đang có gói -> GIA HẠN (Cộng dồn ngày)
            // Nếu gói hiện tại chưa hết hạn, cộng tiếp vào ngày hết hạn cũ
            if (currentSub.getEndDate().isAfter(LocalDateTime.now())) {
                startDate = currentSub.getStartDate(); // Giữ nguyên ngày bắt đầu cũ
                endDate = currentSub.getEndDate().plusDays(plan.getDurationDays());
            } else {
                // Đã hết hạn nhưng status chưa update -> Tính từ hôm nay
                startDate = LocalDateTime.now();
                endDate = startDate.plusDays(plan.getDurationDays());
            }
            // Update thông tin gói mới (nếu user mua gói khác gói cũ)
            currentSub.setPlan(plan);
            currentSub.setEndDate(endDate);
            currentSub.setStatus(SubscriptionStatus.ACTIVE); // Đảm bảo status active
            userSubscriptionRepository.save(currentSub);

        } else {
            // CASE 2: Mua mới (Chưa có gói hoặc gói cũ đã hủy/hết hạn lâu rồi)
            startDate = LocalDateTime.now();
            endDate = startDate.plusDays(plan.getDurationDays());

            // Nếu user đã có record trong bảng sub (nhưng null hoặc inactive) thì update, không thì new
            if (currentSub == null) {
                currentSub = UserSubscription.builder()
                        .user(user)
                        .plan(plan)
                        .startDate(startDate)
                        .endDate(endDate)
                        .status(SubscriptionStatus.ACTIVE)
                        .autoRenew(false)
                        .build();
            } else {
                currentSub.setPlan(plan);
                currentSub.setStartDate(startDate);
                currentSub.setEndDate(endDate);
                currentSub.setStatus(SubscriptionStatus.ACTIVE);
            }
            userSubscriptionRepository.save(currentSub);
        }

        // (Optional) Gửi email thông báo user ở đây
        // emailService.sendPaymentSuccessEmail(user.getEmail(), plan.getName());
    }
}