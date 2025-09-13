package com.xenotask.xeno.repository;

import com.xenotask.xeno.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByTenantTenantIdAndShopifyOrderId(String tenantId, Long shopifyOrderId);
    List<Order> findByTenantTenantId(String tenantId);
    List<Order> findByTenantTenantIdAndFinancialStatus(String tenantId, String financialStatus);
    List<Order> findByTenantTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime start, LocalDateTime end);

    @Query("select o from Order o where o.tenant.tenantId = :tenantId and o.totalPrice > :minTotal")
    List<Order> findHighValueOrders(String tenantId, java.math.BigDecimal minTotal);

    @Query("select coalesce(sum(o.totalPrice),0) from Order o where o.tenant.tenantId=:tenantId and o.createdAt between :start and :end")
    java.math.BigDecimal sumRevenueInRange(@Param("tenantId") String tenantId, @Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    @Query("select o.financialStatus, count(o) from Order o where o.tenant.tenantId=:tenantId group by o.financialStatus")
    java.util.List<Object[]> countByFinancialStatus(@Param("tenantId") String tenantId);

    @Query("select function('date', o.createdAt) as d, coalesce(sum(o.totalPrice),0) from Order o where o.tenant.tenantId=:tenantId and o.createdAt between :start and :end group by function('date', o.createdAt) order by d")
    java.util.List<Object[]> dailyRevenueSeries(@Param("tenantId") String tenantId, @Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);

    @Query("select c.id, c.firstName, c.lastName, coalesce(sum(o.totalPrice),0) as spent from Order o join o.customer c where o.tenant.tenantId=:tenantId group by c.id, c.firstName, c.lastName order by spent desc")
    java.util.List<Object[]> topCustomersByOrders(@Param("tenantId") String tenantId, org.springframework.data.domain.Pageable pageable);
}
