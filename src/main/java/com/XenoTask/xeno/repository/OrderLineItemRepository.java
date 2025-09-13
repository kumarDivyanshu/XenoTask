package com.xenotask.xeno.repository;

import com.xenotask.xeno.entity.OrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineItemRepository extends JpaRepository<OrderLineItem, Integer> {
    List<OrderLineItem> findByOrderId(Integer orderId);
    List<OrderLineItem> findByTenantTenantId(String tenantId);
    List<OrderLineItem> findByProductId(Integer productId);
    List<OrderLineItem> findByVariantId(Integer variantId);
}

