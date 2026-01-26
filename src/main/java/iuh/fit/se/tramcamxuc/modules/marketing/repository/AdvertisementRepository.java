package iuh.fit.se.tramcamxuc.modules.marketing.repository;

import iuh.fit.se.tramcamxuc.modules.marketing.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, UUID> {

    @Query("SELECT a FROM Advertisement a WHERE a.isActive = true " +
            "AND (a.startDate IS NULL OR a.startDate <= CURRENT_TIMESTAMP) " +
            "AND (a.endDate IS NULL OR a.endDate >= CURRENT_TIMESTAMP) " +
            "ORDER BY a.priority DESC")
    List<Advertisement> findActiveAds();

    @Query(value = """
            SELECT * FROM advertisements 
            WHERE ad_type = 'AUDIO' 
            AND is_active = true 
            AND (start_date IS NULL OR start_date <= NOW()) 
            AND (end_date IS NULL OR end_date >= NOW()) 
            ORDER BY RANDOM() 
            LIMIT 1
            """, nativeQuery = true)
    Optional<Advertisement> findRandomActiveAudioAd();
}