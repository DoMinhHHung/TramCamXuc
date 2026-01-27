package iuh.fit.se.tramcamxuc.modules.payment.repository;

import iuh.fit.se.tramcamxuc.modules.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
    Optional<PaymentTransaction> findByOrderCode(Long orderCode);
}