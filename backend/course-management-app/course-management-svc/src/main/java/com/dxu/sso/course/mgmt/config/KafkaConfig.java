package com.dxu.sso.course.mgmt.config;

import com.dxu.sso.common.event.SagaDLTMessage;
import com.dxu.sso.common.event.SagaEvent;
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

import static com.dxu.sso.common.constant.KafkaEventConstants.TOPIC_COURSE_APPLICATION_DLT;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class KafkaConfig {

    private final KafkaRetryProperties retryProperties;

    @Bean
    public DefaultErrorHandler errorHandler(
            @Qualifier("dltKafkaTemplate") KafkaTemplate<String, SagaDLTMessage> dltKafkaTemplate) {
        // Backoff with initial delay 1s, multiplier 2x, max delay 10s, max attempts 3
        ExponentialBackOffWithMaxRetries backoff = new ExponentialBackOffWithMaxRetries(retryProperties.getMaxAttempts());
        backoff.setInitialInterval(retryProperties.getInitialDelay());
        backoff.setMultiplier(retryProperties.getMultiplier());
        backoff.setMaxInterval(retryProperties.getMaxDelay());

        return new DefaultErrorHandler((record, exception) -> {
            log.error("📦 Sending to DLT: {}", exception.getMessage());

            Object value = record.value();
            if (value instanceof SagaEvent originalEvent) {
                SagaDLTMessage dltMessage = SagaDLTMessage.builder()
                        .originalEvent(originalEvent)
                        .errorMessage(exception.getMessage())
                        .stackTrace(Arrays.toString(exception.getStackTrace()))
                        .build();
                dltKafkaTemplate.send(TOPIC_COURSE_APPLICATION_DLT, (String)record.key(), dltMessage);
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
    public ProducerFactory<String, SagaDLTMessage> dltProducerFactory(
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
    public KafkaTemplate<String, SagaDLTMessage> dltKafkaTemplate(
            ProducerFactory<String, SagaDLTMessage> dltProducerFactory) {
        return new KafkaTemplate<>(dltProducerFactory);
    }

}

