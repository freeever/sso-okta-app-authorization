package com.dxu.sso.common.constant;

public interface KafkaEventConstants {

    // Course
    String TOPIC_COURSE_APPLICATION_APPROVED = "course-application-approved";
    String TOPIC_COURSE_ENROLLMENT_CREATED = "course-enrollment-created";
    String TOPIC_COURSE_ENROLLMENT_EXISTED = "course-enrollment-existed";
    String TOPIC_COURSE_ENROLLMENT_FAILED = "course-enrollment-failed";
    String TOPIC_COURSE_APPLICATION_DLT = "course-application-dlt";

    String GROUP_COURSE_APPLICATION = "group-course-app";
    String GROUP_COURSE_MANAGEMENT = "group-course-mgmt";
    String GROUP_DLT_MONITOR = "group-dlt-monitor";

    // Student
    String TOPIC_STUDENT_DELETE_CMDS   = "student-delete-commands";
    String TOPIC_STUDENT_DELETE_REPLIES= "student-delete-replies";

    String GROUP_USER_ADMIN_ORCHESTRATOR = "group-user-admin-orchestrator";
    String GROUP_COURSE_ENROL_STUDENT_DELETE = "group-course-enrol-student-delete";
    String GROUP_COURSE_APP_STUDENT_DELETE = "group-course-app-student-delete";
}
