package com.xenotask.xeno.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncJobMessage implements Serializable {
    private String type; // FULL or INCREMENTAL
    private LocalDateTime since; // for incremental
    private String tenantId; // optional: null => all tenants
}

