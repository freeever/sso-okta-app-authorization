package com.dxu.sso.common.event.student;

public enum StudentDeleteCommandType {
    DELETE_ENROLLMENTS,   // course-management-svc
    RESTORE_ENROLLMENTS,

    DELETE_APPLICATIONS,  // course-application-svc
    RESTORE_APPLICATIONS
}
