package iuh.fit.se.tramcamxuc.modules.subscription.repository;

import iuh.fit.se.tramcamxuc.modules.subscription.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, UUID> {


    @Query("SELECT s FROM UserSubscription s " +
            "WHERE s.user.id = :userId " +
            "AND s.status = 'ACTIVE' " +
            "AND (s.endDate IS NULL OR s.endDate >= CURRENT_TIMESTAMP)")
    Optional<UserSubscription> findActiveSubscriptionByUserId(@Param("userId") UUID userId);


    @Query("SELECT COUNT(s) > 0 FROM UserSubscription s " +
            "WHERE s.user.id = :userId " +
            "AND s.status = 'ACTIVE' " +
            "AND (s.endDate IS NULL OR s.endDate >= CURRENT_TIMESTAMP) " +
            "AND s.plan.price > 0")
    boolean isPremiumUser(@Param("userId") UUID userId);

    Optional<UserSubscription> findByUserId(UUID userId);
}