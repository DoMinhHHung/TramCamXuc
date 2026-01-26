package iuh.fit.se.tramcamxuc.modules.subscription.repository;

import iuh.fit.se.tramcamxuc.modules.subscription.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    boolean existsByName(String name);

    Optional<SubscriptionPlan> findByName(String name);
}