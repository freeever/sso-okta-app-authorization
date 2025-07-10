package com.dxu.sso.course.application.config;

import com.dxu.sso.common.event.CourseApplicationApprovedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, CourseApplicationApprovedEvent> kafkaTemplate(
            ProducerFactory<String, CourseApplicationApprovedEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}

