package com.xenotask.xeno.service;

import com.xenotask.xeno.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final TenantService tenantService;


    @Cacheable(value = "revenue:range", key = "#tenantHeader + ':' + #start + ':' + #end")
    public BigDecimal revenueInRange(String tenantHeader, LocalDateTime start, LocalDateTime end) {
        String tenantId = tenantService.resolveTenantIdOrDomain();
        return orderRepository.sumRevenueInRange(tenantId, start, end);
    }

    @Cacheable(value = "revenue:daily", key = "#tenantHeader + ':' + #startDate + ':' + #endDate")
    public List<Map<String,Object>> dailyRevenue(String tenantHeader, LocalDate startDate, LocalDate endDate) {
        String tenantId = tenantService.resolveTenantIdOrDomain();
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay().minusSeconds(1);
        return orderRepository.dailyRevenueSeries(tenantId, start, end).stream()
                .map(row -> Map.of(
                        "date", row[0].toString(),
                        "revenue", row[1]))
                .toList();
    }

    @Cacheable(value = "orders:statusBreakdown", key = "#tenantHeader")
    public List<Map<String,Object>> statusBreakdown(String tenantHeader) {
        String tenantId = tenantService.resolveTenantIdOrDomain();
        return orderRepository.countByFinancialStatus(tenantId).stream()
                .map(r -> Map.of("status", r[0], "count", r[1]))
                .toList();
    }

    @Cacheable(value = "customers:top", key = "#tenantHeader + ':' + #limit")
    public List<Map<String,Object>> topCustomers(String tenantHeader, int limit) {
        String tenantId = tenantService.resolveTenantIdOrDomain();
        return orderRepository.topCustomersByOrders(tenantId, PageRequest.of(0, limit)).stream()
                .map(r -> Map.of(
                        "customerId", r[0],
                        "firstName", r[1],
                        "lastName", r[2],
                        "totalSpent", r[3]))
                .toList();
    }
}

