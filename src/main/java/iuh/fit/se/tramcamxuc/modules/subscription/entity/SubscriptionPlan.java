package iuh.fit.se.tramcamxuc.modules.subscription.entity;

import iuh.fit.se.tramcamxuc.common.BaseEntity;
import iuh.fit.se.tramcamxuc.common.converter.JsonAttributeConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    private Integer durationDays;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonAttributeConverter.class)
    private Map<String, Object> features = new HashMap<>();

    private boolean isActive = true;
}