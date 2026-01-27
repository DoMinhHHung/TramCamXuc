package iuh.fit.se.tramcamxuc.modules.subscription.service;

import iuh.fit.se.tramcamxuc.modules.subscription.dto.request.CreatePlanRequest;
import iuh.fit.se.tramcamxuc.modules.subscription.entity.SubscriptionPlan;
import iuh.fit.se.tramcamxuc.modules.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    List<SubscriptionPlan> getAllPlans();

    SubscriptionPlan getPlanById(UUID id);

    SubscriptionPlan createPlan(CreatePlanRequest request);

    SubscriptionPlan updatePlan(UUID id, CreatePlanRequest request);

    void toggleActive(UUID id);

    void activateSubscription(User user, SubscriptionPlan plan);
}