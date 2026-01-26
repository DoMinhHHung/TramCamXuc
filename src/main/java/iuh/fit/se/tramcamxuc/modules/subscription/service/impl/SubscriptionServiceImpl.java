package iuh.fit.se.tramcamxuc.modules.subscription.service.impl;

import iuh.fit.se.tramcamxuc.common.exception.AppException;
import iuh.fit.se.tramcamxuc.common.exception.ResourceNotFoundException;
import iuh.fit.se.tramcamxuc.modules.subscription.dto.request.CreatePlanRequest;
import iuh.fit.se.tramcamxuc.modules.subscription.entity.SubscriptionPlan;
import iuh.fit.se.tramcamxuc.modules.subscription.repository.SubscriptionPlanRepository;
import iuh.fit.se.tramcamxuc.modules.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPlanRepository planRepository;

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
}