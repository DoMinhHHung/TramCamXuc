package iuh.fit.se.tramcamxuc.modules.payment.repository;

import iuh.fit.se.tramcamxuc.modules.admin.dto.projection.RevenueByPlanProjection;
import iuh.fit.se.tramcamxuc.modules.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
    Optional<PaymentTransaction> findByOrderCode(Long orderCode);

    @Query("SELECT SUM(p.amount) FROM PaymentTransaction p WHERE p.status = 'SUCCESS'")
    Double sumTotalRevenue();

    @Query("SELECT p.plan.name as planName, SUM(p.amount) as totalAmount, COUNT(p) as transactionCount " +
            "FROM PaymentTransaction p " +
            "WHERE p.status = 'SUCCESS' " +
            "GROUP BY p.plan.name")
    List<RevenueByPlanProjection> getRevenueByPlan();
}