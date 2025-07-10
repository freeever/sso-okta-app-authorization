package com.dxu.sso.course.mgmt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaConsumerConfig {

    private final KafkaRetryProperties retryProperties;

    @Bean
    public DefaultErrorHandler errorHandler() {
        // Backoff with initial delay 1s, multiplier 2x, max delay 10s, max attempts 3
        ExponentialBackOffWithMaxRetries backoff =
                new ExponentialBackOffWithMaxRetries(retryProperties.getMaxAttempts());
        backoff.setInitialInterval(retryProperties.getInitialDelay());
        backoff.setMultiplier(retryProperties.getMultiplier());
        backoff.setMaxInterval(retryProperties.getMaxDelay());

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backoff);

        // Log the exception
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("❌ Retry {} for record: {}", deliveryAttempt, record, ex);
        });

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}

