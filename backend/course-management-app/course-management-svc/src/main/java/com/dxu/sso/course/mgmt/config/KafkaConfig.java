package com.dxu.sso.course.mgmt.config;

import com.dxu.sso.common.event.CourseApplicationApprovedDLTMessage;
import com.dxu.sso.common.event.CourseApplicationApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaConfig {

    private final KafkaRetryProperties retryProperties;

    @Bean
    public DefaultErrorHandler errorHandler(
            @Qualifier("dltKafkaTemplate") KafkaTemplate<String, CourseApplicationApprovedDLTMessage> dltKafkaTemplate) {
        // Backoff with initial delay 1s, multiplier 2x, max delay 10s, max attempts 3
        ExponentialBackOffWithMaxRetries backoff = new ExponentialBackOffWithMaxRetries(retryProperties.getMaxAttempts());
        backoff.setInitialInterval(retryProperties.getInitialDelay());
        backoff.setMultiplier(retryProperties.getMultiplier());
        backoff.setMaxInterval(retryProperties.getMaxDelay());

        return new DefaultErrorHandler((record, exception) -> {
            log.error("📦 Sending to DLT: {}", exception.getMessage());

            Object value = record.value();
            if (value instanceof CourseApplicationApprovedEvent originalEvent) {
                CourseApplicationApprovedDLTMessage dltMessage = CourseApplicationApprovedDLTMessage.builder()
                        .originalEvent(originalEvent)
                        .errorMessage(exception.getMessage())
                        .stackTrace(Arrays.toString(exception.getStackTrace()))
                        .build();
                dltKafkaTemplate.send("course-application-approved-dlt", (String)record.key(), dltMessage);
            } else {
                log.warn("⚠️ Unrecognized message. Skipping DLT fallback.");
            }
        }, backoff);
    }

    /**
     * registers a custom Kafka listener container factory that tells Spring Kafka to:
     *  - Use your consumerFactory to build consumers
     *  - Use your DefaultErrorHandler (with retries + DLT logic)
     */
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

    // ✅ Producer Factory for DLT messages
    @Bean
    public ProducerFactory<String, CourseApplicationApprovedDLTMessage> dltProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // ✅ Producer Factory for generic Object messages
    @Bean
    public ProducerFactory<String, Object> producerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Shared KafkaTemplate for sending normal messages
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> factory) {
        return new KafkaTemplate<>(factory);
    }

    /**
     * Dedicated KafkaTemplate for sending DLT messages
     */
    @Bean
    public KafkaTemplate<String, CourseApplicationApprovedDLTMessage> dltKafkaTemplate(
            ProducerFactory<String, CourseApplicationApprovedDLTMessage> dltProducerFactory) {
        return new KafkaTemplate<>(dltProducerFactory);
    }

}

