package com.dxu.sso.common.constant;

public interface KafkaEventConstants {

    String TOPIC_COURSE_APPLICATION_APPROVED = "course-application-approved";
    String TOPIC_COURSE_ENROLLMENT_CREATED = "course-enrollment-created";
    String TOPIC_COURSE_ENROLLMENT_EXISTED = "course-enrollment-existed";
    String TOPIC_COURSE_ENROLLMENT_FAILED = "course-enrollment-failed";
    String TOPIC_COURSE_APPLICATION_DLT = "course-application-dlt";

    String GROUP_COURSE_APPLICATION = "group-course-app";
    String GROUP_COURSE_MANAGEMENT = "group-course-mgmt";
    String GROUP_DLT_MONITOR = "group-dlt-monitor";
}
