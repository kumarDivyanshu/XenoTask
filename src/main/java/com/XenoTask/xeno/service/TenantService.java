package com.xenotask.xeno.service;

import com.xenotask.xeno.entity.Tenant;
import com.xenotask.xeno.repository.TenantRepository;
import com.xenotask.xeno.exception.TenantNotFoundException;
import com.xenotask.xeno.security.CryptoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TenantService {
    private final TenantRepository tenantRepository;
    private final CryptoService cryptoService;

    public TenantService(TenantRepository tenantRepository, CryptoService cryptoService) {
        this.tenantRepository = tenantRepository;
        this.cryptoService = cryptoService;
    }

    public Tenant getRequiredByTenantId(String tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException(tenantId));
    }

    public Optional<Tenant> findByTenantId(String tenantId) {
        return tenantRepository.findById(tenantId);
    }

    public Optional<Tenant> findByShopDomain(String shopDomain) {
        return tenantRepository.findByShopDomain(shopDomain);
    }

    @Transactional
    public Tenant onboardTenant(String shopDomain, String accessToken) {
        String tenantId = UUID.randomUUID().toString();
        Tenant tenant = tenantRepository.findById(tenantId).orElseGet(Tenant::new);
        tenant.setTenantId(tenantId);
        tenant.setShopDomain(shopDomain);
        // Encrypt before storing
        tenant.setAccessToken(cryptoService.encrypt(accessToken));
        tenant.setIsActive(true);
        tenant.setCreatedAt(tenant.getCreatedAt() == null ? LocalDateTime.now() : tenant.getCreatedAt());
        tenant.setUpdatedAt(LocalDateTime.now());
        return tenantRepository.save(tenant);
    }

    @Transactional
    public void updateAccessToken(String tenantId, String newToken) {
        Tenant tenant = getRequiredByTenantId(tenantId);
        tenant.setAccessToken(cryptoService.encrypt(newToken));
        tenant.setUpdatedAt(LocalDateTime.now());
    }

    public String resolveTenantIdOrDomain() {
        String ctx = TenantContext.getTenantId();
        if (ctx == null) throw new TenantNotFoundException("(header missing)");
        // First attempt direct tenant id, else treat as shop domain
        if (tenantRepository.existsById(ctx)) return ctx;
        return tenantRepository.findByShopDomain(ctx)
                .map(Tenant::getTenantId)
                .orElseThrow(() -> new TenantNotFoundException(ctx));
    }

    public List<Tenant> listActiveTenants() {
        return tenantRepository.findAllByIsActiveTrue();
    }
}
