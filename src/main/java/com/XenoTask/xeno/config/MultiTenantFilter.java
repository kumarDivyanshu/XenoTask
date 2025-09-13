package com.xenotask.xeno.config;

import com.xenotask.xeno.service.TenantContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Simple filter that extracts tenant id from header X-Tenant-ID or X-Shop-Domain (shop domain maps via service later).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MultiTenantFilter implements Filter {
    public static final String TENANT_HEADER = "X-Tenant-ID";
    public static final String SHOP_HEADER = "X-Shop-Domain";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            if (request instanceof HttpServletRequest http) {
                String tenantId = http.getHeader(TENANT_HEADER);
                if (tenantId == null || tenantId.isBlank()) {
                    tenantId = http.getHeader(SHOP_HEADER); // may be shop domain, translation done in service methods
                }
                if (tenantId != null && !tenantId.isBlank()) {
                    TenantContext.setTenantId(tenantId.trim());
                }
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}

