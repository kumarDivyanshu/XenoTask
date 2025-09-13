package com.xenotask.xeno.repository;

import com.xenotask.xeno.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    Optional<ProductVariant> findByTenantTenantIdAndShopifyVariantId(String tenantId, Long shopifyVariantId);
    List<ProductVariant> findByProductId(Integer productId);
    List<ProductVariant> findByTenantTenantId(String tenantId);
    List<ProductVariant> findByTenantTenantIdAndSku(String tenantId, String sku);
}

