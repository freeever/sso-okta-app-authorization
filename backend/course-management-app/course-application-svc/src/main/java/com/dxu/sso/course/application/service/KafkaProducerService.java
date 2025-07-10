package com.dxu.sso.course.application.service;

import com.dxu.sso.common.event.CourseApplicationApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, CourseApplicationApprovedEvent> kafkaTemplate;

    private static final String TOPIC = "course-application-approved";

    public void sendApplicationApprovedEvent(CourseApplicationApprovedEvent event) {
        log.info("send application approved event. course: {} student: {}", event.getCourseId(), event.getStudentId());
        kafkaTemplate.send(TOPIC, event);
    }
}

