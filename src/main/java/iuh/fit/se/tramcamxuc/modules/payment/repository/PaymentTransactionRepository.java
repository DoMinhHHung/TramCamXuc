package iuh.fit.se.tramcamxuc.modules.payment.repository;

import iuh.fit.se.tramcamxuc.modules.admin.dto.RevenueByPlanDTO;
import iuh.fit.se.tramcamxuc.modules.admin.dto.RevenueStatsResponse;
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

    // 2. Thống kê theo Gói cước (Plan)
    @Query("SELECT new iuh.fit.se.tramcamxuc.modules.admin.dto.RevenueByPlanDTO(p.plan.name, SUM(p.amount), COUNT(p)) " +
            "FROM PaymentTransaction p " +
            "WHERE p.status = 'SUCCESS' " +
            "GROUP BY p.plan.name")
    List<RevenueByPlanDTO> getRevenueByPlan();
}