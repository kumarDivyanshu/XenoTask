package com.xenotask.xeno.messaging;

import com.xenotask.xeno.config.RabbitConfig;
import com.xenotask.xeno.entity.Tenant;
import com.xenotask.xeno.service.TenantService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "sync.messaging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TenantQueueProvider {
    private static final Logger log = LoggerFactory.getLogger(TenantQueueProvider.class);

    private final TenantService tenantService;
    private final AmqpAdmin amqpAdmin; // Boot auto-configured

    private final List<String> queueNames = new CopyOnWriteArrayList<>();

    public String[] getQueueNames() { return queueNames.toArray(new String[0]); }

    private String queueName(String tenantId) { return "sync.jobs." + tenantId; }

    @PostConstruct
    public void init() {
        try {
            List<Tenant> tenants = tenantService.listActiveTenants();
            for (Tenant t : tenants) { ensureQueue(t.getTenantId()); }
            log.info("TenantQueueProvider initialized with {} queues", queueNames.size());
        } catch (Exception e) {
            log.warn("Skipping initial tenant queue declaration (broker unavailable?): {}", e.getMessage());
        }
    }

    public void ensureQueue(String tenantId) {
        String qn = queueName(tenantId);
        if (queueNames.contains(qn)) return;
        Map<String, Object> args = Map.of(
                "x-dead-letter-exchange", RabbitConfig.DLX_EXCHANGE,
                "x-dead-letter-routing-key", RabbitConfig.DLQ_ROUTING_KEY
        );
        Queue q = QueueBuilder.durable(qn).withArguments(args).build();
        amqpAdmin.declareQueue(q);
        queueNames.add(qn);
        log.info("Declared per-tenant sync queue {}", qn);
    }
}
