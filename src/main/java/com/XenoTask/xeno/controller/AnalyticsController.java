package com.xenotask.xeno.controller;

import com.xenotask.xeno.service.AnalyticsService;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String,Object>> revenue(@RequestHeader("X-Tenant-ID") String tenant,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        var total = analyticsService.revenueInRange(tenant, start, end);
        return ResponseEntity.ok(Map.of("totalRevenue", total));
    }

    @GetMapping("/revenue/daily")
    public ResponseEntity<List<Map<String,Object>>> daily(@RequestHeader("X-Tenant-ID") String tenant,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(analyticsService.dailyRevenue(tenant, start, end));
    }

    @GetMapping("/orders/status-breakdown")
    public ResponseEntity<List<Map<String,Object>>> status(@RequestHeader("X-Tenant-ID") String tenant) {
        log.info("Fetching status breakdown for tenant: {}", tenant);
        return ResponseEntity.ok(analyticsService.statusBreakdown(tenant));
    }

    @GetMapping("/customers/top")
    public ResponseEntity<List<Map<String,Object>>> topCustomers(@RequestHeader("X-Tenant-ID") String tenant,
                                                                 @RequestParam(defaultValue = "5") @Min(1) int limit) {
        return ResponseEntity.ok(analyticsService.topCustomers(tenant, limit));
    }
}

