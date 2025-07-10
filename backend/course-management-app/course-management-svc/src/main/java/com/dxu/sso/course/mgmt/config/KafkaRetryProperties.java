package com.dxu.sso.course.mgmt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom.kafka.retry")
@Data
public class KafkaRetryProperties {

    private long initialDelay;
    private double multiplier;
    private long maxDelay;
    private int maxAttempts;
}
