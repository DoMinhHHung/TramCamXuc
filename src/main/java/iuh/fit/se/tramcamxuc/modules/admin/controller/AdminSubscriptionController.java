package iuh.fit.se.tramcamxuc.modules.subscription.controller;

import iuh.fit.se.tramcamxuc.common.exception.dto.ApiResponse;
import iuh.fit.se.tramcamxuc.modules.subscription.dto.request.CreatePlanRequest;
import iuh.fit.se.tramcamxuc.modules.subscription.entity.SubscriptionPlan;
import iuh.fit.se.tramcamxuc.modules.subscription.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionPlan>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getAllPlans()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlan>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.getPlanById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionPlan>> create(@RequestBody @Valid CreatePlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.createPlan(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionPlan>> update(@PathVariable UUID id, @RequestBody @Valid CreatePlanRequest request) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionService.updatePlan(id, request)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<String>> toggle(@PathVariable UUID id) {
        subscriptionService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.success("Đổi trạng thái gói cước thành công"));
    }
}